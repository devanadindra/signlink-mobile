package kamus

import (
	"context"
	"fmt"
	"os"
	"path/filepath"
	"time"

	"github.com/devanadindraa/NTTH-Store/back-end/database"
	"github.com/devanadindraa/NTTH-Store/back-end/utils/config"
	"github.com/devanadindraa/NTTH-Store/back-end/utils/dbselector"
	fileutils "github.com/devanadindraa/NTTH-Store/back-end/utils/file"
	"github.com/google/uuid"
)

type Service interface {
	GetKamus(ctx context.Context, kategori string) (*[]KamusRes, error)
	AddKamus(ctx context.Context, req KamusReq) error
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
