package kamus

import (
	"context"
	"errors"
	"fmt"
	"os"
	"path/filepath"
	"strings"
	"time"

	"github.com/devanadindra/signlink-mobile/back-end/database"
	apierror "github.com/devanadindra/signlink-mobile/back-end/utils/api-error"
	"github.com/devanadindra/signlink-mobile/back-end/utils/config"
	"github.com/devanadindra/signlink-mobile/back-end/utils/constants"
	"github.com/devanadindra/signlink-mobile/back-end/utils/dbselector"
	fileutils "github.com/devanadindra/signlink-mobile/back-end/utils/file"
	"github.com/google/uuid"
	"gorm.io/gorm"
)

type Service interface {
	GetKamus(ctx context.Context, kategori string) (*[]KamusRes, error)
	AddKamus(ctx context.Context, req KamusReq) error
	DeleteKamus(ctx context.Context, kamusId string) error
	GetAllKamus(ctx context.Context, input GetAllKamusReq, filter constants.FilterReq) (*[]KamusRes, int64, error)
	GetKamusByArti(ctx context.Context, arti string) (*Kamus, error)
}

type service struct {
	authConfig config.Auth
	dbSelector *dbselector.DBService
	CustomerDB *database.CustomerDB
	AdminDB    *database.AdminDB
}

func NewService(config *config.Config, dbSelector *dbselector.DBService, CustomerDB *database.CustomerDB, AdminDB *database.AdminDB) Service {
	return &service{
		authConfig: config.Auth,
		dbSelector: dbSelector,
		CustomerDB: CustomerDB,
		AdminDB:    AdminDB,
	}
}

func (s *service) GetKamus(ctx context.Context, kategori string) (*[]KamusRes, error) {
	db, err := s.dbSelector.GetDBByRole(ctx)
	if err != nil {
		return nil, err
	}

	var kamusList []Kamus
	err = db.WithContext(ctx).
		Where("kategori = ?", kategori).
		Find(&kamusList).Error
	if err != nil {
		return nil, err
	}

	res := make([]KamusRes, len(kamusList))
	for i, k := range kamusList {
		res[i] = KamusRes{
			ID:       k.ID.String(),
			Arti:     k.Arti,
			Kategori: k.Kategori,
			VideoUrl: k.VideoUrl,
		}
	}

	return &res, nil
}

func (s *service) AddKamus(ctx context.Context, req KamusReq) error {
	db, err := s.dbSelector.GetDBByRole(ctx)
	if err != nil {
		return err
	}

	tx := db.WithContext(ctx).Begin()
	if tx.Error != nil {
		return tx.Error
	}

	defer func() {
		if r := recover(); r != nil {
			tx.Rollback()
		}
	}()

	kamusId := uuid.New()

	file, err := req.Video.Open()
	if err != nil {
		tx.Rollback()
		return fmt.Errorf("failed to open video: %w", err)
	}
	defer file.Close()

	ext := filepath.Ext(req.Video.Filename)
	filename, err := fileutils.GenerateMediaName(kamusId.String())
	if err != nil {
		tx.Rollback()
		return fmt.Errorf("error generating image name: %v", err)
	}

	filename = fmt.Sprintf("%s%s", filename, ext)
	path := filepath.Join("kamus_videos", req.Kategori, filename)
	videoURL := "/kamus_videos/" + req.Kategori + "/" + filename

	if err := os.MkdirAll(filepath.Dir(path), os.ModePerm); err != nil {
		tx.Rollback()
		return fmt.Errorf("failed to create directory: %w", err)
	}

	if err := fileutils.SaveMedia(ctx, req.Video, path); err != nil {
		tx.Rollback()
		return err
	}

	kamus := Kamus{
		ID:        kamusId,
		Arti:      req.Arti,
		Kategori:  req.Kategori,
		VideoUrl:  videoURL,
		CreatedAt: time.Now(),
		UpdatedAt: time.Now(),
	}

	if err := tx.Create(&kamus).Error; err != nil {
		tx.Rollback()
		return err
	}

	return tx.Commit().Error
}

func (s *service) DeleteKamus(ctx context.Context, kamusId string) error {
	db, err := s.dbSelector.GetDBByRole(ctx)
	if err != nil {
		return err
	}

	var kamus Kamus
	if err := db.WithContext(ctx).First(&kamus, "id = ?", kamusId).Error; err != nil {
		return fmt.Errorf("kamus not found: %v", err)
	}

	cleanFilePath := strings.TrimPrefix(kamus.VideoUrl, "/")
	if err := os.Remove(cleanFilePath); err != nil {
		if !os.IsNotExist(err) {
			return fmt.Errorf("failed to delete video file %s: %v", cleanFilePath, err)
		}
	}

	if err := db.WithContext(ctx).Delete(&kamus).Error; err != nil {
		return fmt.Errorf("error deleting kamus: %v", err)
	}

	return nil
}

func (s *service) GetAllKamus(ctx context.Context, input GetAllKamusReq, filter constants.FilterReq) (*[]KamusRes, int64, error) {
	var total int64
	var kamusList []Kamus

	db, err := s.dbSelector.GetDBByRole(ctx)
	if err != nil {
		return nil, 0, err
	}

	query := db.WithContext(ctx).Model(&Kamus{})

	// Filtering keyword
	if input.Keyword != "" {
		query = query.Where(
			"arti ILIKE ? OR kategori ILIKE ? ",
			"%"+input.Keyword+"%",
			"%"+input.Keyword+"%",
		)
	}

	// Hitung total data sebelum limit & offset
	if err := query.Count(&total).Error; err != nil {
		return nil, 0, err
	}

	// Pagination
	offset := (filter.Page - 1) * filter.Limit

	if err := query.
		Order(fmt.Sprintf("%s %s", filter.OrderBy, filter.SortOrder)).
		Limit(int(filter.Limit)).
		Offset(int(offset)).
		Find(&kamusList).Error; err != nil {
		return nil, 0, err
	}

	res := make([]KamusRes, len(kamusList))
	for i, k := range kamusList {
		res[i] = KamusRes{
			ID:       k.ID.String(),
			Arti:     k.Arti,
			Kategori: k.Kategori,
			VideoUrl: k.VideoUrl,
		}
	}

	return &res, total, nil
}

func (s *service) GetKamusByArti(ctx context.Context, arti string) (*Kamus, error) {
	db, err := s.dbSelector.GetDBByRole(ctx)
	if err != nil {
		return nil, err
	}

	var kamus Kamus
	if err := db.WithContext(ctx).First(&kamus, "arti = ?", arti).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, apierror.ArtiNotFound(arti)
		}
		return nil, err
	}

	return &kamus, nil
}
