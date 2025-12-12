package kuis

import (
	"context"
	"errors"
	"fmt"
	"strings"
	"time"

	"github.com/devanadindra/signlink-mobile/back-end/database"
	"github.com/devanadindra/signlink-mobile/back-end/domains/kamus"
	apierror "github.com/devanadindra/signlink-mobile/back-end/utils/api-error"
	"github.com/devanadindra/signlink-mobile/back-end/utils/config"
	"github.com/devanadindra/signlink-mobile/back-end/utils/constants"
	contextUtil "github.com/devanadindra/signlink-mobile/back-end/utils/context"
	"github.com/devanadindra/signlink-mobile/back-end/utils/dbselector"
	"github.com/google/uuid"
	"golang.org/x/text/cases"
	"golang.org/x/text/language"
	"gorm.io/gorm"
)

type Service interface {
	GetAllKuis(ctx context.Context, input GetAllKuisReq, filter constants.FilterReq) (*[]KuisRes, int64, error)
	GetKuisById(ctx context.Context, kuisId string) (*KuisByIdRes, error)
	AddKuis(ctx context.Context, req KuisReq) error
	DeleteKuis(ctx context.Context, kuisId string) error
	AddStatsKuis(ctx context.Context, req StatsKuisReq) error
	GetStatsByUserId(ctx context.Context) (*[]StatsKuisRes, error)
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

func (s *service) GetAllKuis(ctx context.Context, input GetAllKuisReq, filter constants.FilterReq) (*[]KuisRes, int64, error) {
	var total int64
	var kuisList []ModulKuis

	token, err := contextUtil.GetTokenClaims(ctx)
	if err != nil {
		return nil, 0, err
	}

	db, err := s.dbSelector.GetDBByRole(ctx)
	if err != nil {
		return nil, 0, err
	}

	query := db.WithContext(ctx).Model(&ModulKuis{})

	if err := query.Count(&total).Error; err != nil {
		return nil, 0, err
	}

	offset := (filter.Page - 1) * filter.Limit

	if err := query.
		Order(fmt.Sprintf("%s %s", filter.OrderBy, filter.SortOrder)).
		Preload("StatsKuis", "user_id = ?", token.Claims.UserID).
		Limit(int(filter.Limit)).
		Offset(int(offset)).
		Find(&kuisList).Error; err != nil {
		return nil, 0, err
	}

	res := make([]KuisRes, len(kuisList))
	for i, k := range kuisList {
		res[i] = KuisRes{
			ID:         k.ID.String(),
			Name:       k.Name,
			TotalSoal:  k.TotalSoal,
			BatasWaktu: k.BatasWaktu,
			IsDone:     len(k.StatsKuis) > 0,
		}
	}

	return &res, total, nil
}

func (s *service) GetKuisById(ctx context.Context, kuisId string) (*KuisByIdRes, error) {
	db, err := s.dbSelector.GetDBByRole(ctx)
	if err != nil {
		return nil, err
	}

	var kuis ModulKuis
	err = db.WithContext(ctx).
		Preload("SoalKuis").
		Preload("SoalKuis.OpsiKuis").
		Where("id =?", kuisId).
		Find(&kuis).Error
	if err != nil {
		return nil, err
	}

	soalRes := make([]SoalKuisRes, len(kuis.SoalKuis))
	for i, soal := range kuis.SoalKuis {
		opsiRes := make([]OpsiKuisRes, len(soal.OpsiKuis))
		for j, opsi := range soal.OpsiKuis {
			opsiRes[j] = OpsiKuisRes{
				ID:     opsi.ID.String(),
				SoalID: opsi.SoalID.String(),
				Label:  opsi.Label,
				Text:   opsi.Text,
			}
		}

		soalRes[i] = SoalKuisRes{
			ID:           soal.ID.String(),
			ModulID:      soal.ModulID.String(),
			VideoURL:     soal.VideoURL,
			Soal:         soal.Soal,
			JawabanBenar: soal.JawabanBenar,
			OpsiKuis:     opsiRes,
		}
	}

	res := KuisByIdRes{
		ID:         kuis.ID.String(),
		Name:       kuis.Name,
		TotalSoal:  kuis.TotalSoal,
		BatasWaktu: kuis.BatasWaktu,
		SoalKuis:   soalRes,
	}

	return &res, nil
}

func (s *service) AddKuis(ctx context.Context, req KuisReq) error {
	titleCaser := cases.Title(language.Indonesian)

	db, err := s.dbSelector.GetDBByRole(ctx)
	if err != nil {
		return apierror.FromErr(err)
	}

	tx := db.Begin()
	if tx.Error != nil {
		return tx.Error
	}

	modulID := uuid.New()

	kuis := ModulKuis{
		ID:         modulID,
		Name:       req.Name,
		TotalSoal:  len(req.SoalKuis),
		BatasWaktu: req.BatasWaktu,
		CreatedAt:  time.Now(),
		UpdatedAt:  time.Now(),
	}

	if err := tx.WithContext(ctx).Create(&kuis).Error; err != nil {
		tx.Rollback()
		return apierror.FromErr(err)
	}

	soalList := make([]SoalKuis, 0, len(req.SoalKuis))
	for _, soal := range req.SoalKuis {
		jawabanBenar := titleCaser.String(soal.JawabanBenar)

		var kamusItem kamus.Kamus
		if err := tx.WithContext(ctx).First(&kamusItem, "arti = ?", jawabanBenar).Error; err != nil {
			if errors.Is(err, gorm.ErrRecordNotFound) {
				tx.Rollback()
				return apierror.ArtiNotFound(jawabanBenar)
			}
			tx.Rollback()
			return err
		}

		opsiList := make([]OpsiKuis, 0, len(soal.OpsiKuis))
		soalID := uuid.New()
		for _, opsi := range soal.OpsiKuis {
			opsiList = append(opsiList, OpsiKuis{
				SoalID: soalID,
				Label:  strings.ToUpper(opsi.Label),
				Text:   titleCaser.String(opsi.Text),
			})
		}
		soalList = append(soalList, SoalKuis{
			ID:           soalID,
			ModulID:      modulID,
			VideoURL:     kamusItem.VideoUrl,
			Soal:         soal.Soal,
			JawabanBenar: jawabanBenar,
			OpsiKuis:     opsiList,
		})
	}

	if err := tx.WithContext(ctx).Create(&soalList).Error; err != nil {
		tx.Rollback()
		return apierror.FromErr(err)
	}

	return tx.Commit().Error
}

func (s *service) DeleteKuis(ctx context.Context, kuisId string) error {
	db, err := s.dbSelector.GetDBByRole(ctx)
	if err != nil {
		return apierror.FromErr(err)
	}

	var kuis ModulKuis
	if err := db.WithContext(ctx).First(&kuis, "id = ?", kuisId).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return apierror.LatihanNotFound(kuisId)
		}
		return apierror.FromErr(err)
	}

	if err := db.WithContext(ctx).Delete(&kuis).Error; err != nil {
		return apierror.FromErr(err)
	}

	return nil
}

func (s *service) AddStatsKuis(ctx context.Context, req StatsKuisReq) error {
	token, err := contextUtil.GetTokenClaims(ctx)
	if err != nil {
		return err
	}

	db, err := s.dbSelector.GetDBByRole(ctx)
	if err != nil {
		return apierror.FromErr(err)
	}

	var kuis ModulKuis
	if err := db.WithContext(ctx).First(&kuis, "id = ?", req.KuisID).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return apierror.LatihanNotFound(req.KuisID)
		}
		return apierror.FromErr(err)
	}

	statsKuis := StatsKuis{
		KuisID:    kuis.ID,
		UserID:    token.Claims.UserID,
		Score:     req.Score,
		CreatedAt: time.Now(),
		UpdatedAt: time.Now(),
	}

	if err := db.WithContext(ctx).Create(&statsKuis).Error; err != nil {
		return apierror.FromErr(err)
	}

	return nil
}

func (s *service) GetStatsByUserId(ctx context.Context) (*[]StatsKuisRes, error) {
	token, err := contextUtil.GetTokenClaims(ctx)
	if err != nil {
		return nil, err
	}

	db, err := s.dbSelector.GetDBByRole(ctx)
	if err != nil {
		return nil, err
	}

	subQuery := db.
		Table("stats_kuis").
		Select("MAX(created_at) AS max").
		Where("user_id = ?", token.Claims.UserID).
		Group("latihan_id")

	var stats []StatsKuis
	err = db.WithContext(ctx).
		Table("stats_latihan s").
		Select("s.*").
		Joins("JOIN (?) latest ON s.created_at = latest.max", subQuery).
		Where("s.user_id = ?", token.Claims.UserID).
		Order("s.created_at DESC").
		Find(&stats).Error
	if err != nil {
		return nil, err
	}

	// response
	res := make([]StatsKuisRes, len(stats))
	for i, stat := range stats {
		var kuis ModulKuis
		err := db.WithContext(ctx).
			Where("id = ?", stat.KuisID).
			First(&kuis).Error
		if err != nil {
			return nil, err
		}

		res[i] = StatsKuisRes{
			ID:        stat.ID.String(),
			KuisID:    stat.KuisID.String(),
			KuisName:  kuis.Name,
			TotalSoal: kuis.TotalSoal,
			Score:     stat.Score,
		}
	}

	return &res, nil
}
