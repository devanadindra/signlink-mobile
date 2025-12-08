package latihan

import (
	"context"
	"errors"
	"fmt"
	"strings"
	"time"

	"github.com/devanadindraa/NTTH-Store/back-end/database"
	apierror "github.com/devanadindraa/NTTH-Store/back-end/utils/api-error"
	"github.com/devanadindraa/NTTH-Store/back-end/utils/config"
	"github.com/devanadindraa/NTTH-Store/back-end/utils/constants"
	"github.com/devanadindraa/NTTH-Store/back-end/utils/dbselector"
	"github.com/google/uuid"
	"gorm.io/gorm"
)

type Service interface {
	GetAllLatihan(ctx context.Context, input GetAllLatihanReq, filter constants.FilterReq) (*[]LatihanRes, int64, error)
	GetLatihanById(ctx context.Context, latihanId string) (*LatihanByIdRes, error)
	AddLatihan(ctx context.Context, req LatihanReq) error
	DeleteLatihan(ctx context.Context, latihanId string) error
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

func (s *service) GetAllLatihan(ctx context.Context, input GetAllLatihanReq, filter constants.FilterReq) (*[]LatihanRes, int64, error) {
	var total int64
	var latihanList []Latihan

	db, err := s.dbSelector.GetDBByRole(ctx)
	if err != nil {
		return nil, 0, err
	}

	query := db.WithContext(ctx).Model(&Latihan{})

	if err := query.Count(&total).Error; err != nil {
		return nil, 0, err
	}

	offset := (filter.Page - 1) * filter.Limit

	if err := query.
		Order(fmt.Sprintf("%s %s", filter.OrderBy, filter.SortOrder)).
		Limit(int(filter.Limit)).
		Offset(int(offset)).
		Find(&latihanList).Error; err != nil {
		return nil, 0, err
	}

	res := make([]LatihanRes, len(latihanList))
	for i, l := range latihanList {
		res[i] = LatihanRes{
			ID:        l.ID.String(),
			Name:      l.Name,
			TotalSoal: l.TotalSoal,
		}
	}

	return &res, total, nil
}

func (s *service) GetLatihanById(ctx context.Context, latihanId string) (*LatihanByIdRes, error) {
	db, err := s.dbSelector.GetDBByRole(ctx)
	if err != nil {
		return nil, err
	}

	var latihan Latihan
	err = db.WithContext(ctx).
		Preload("SoalLatihan").
		Where("id =?", latihanId).
		Find(&latihan).Error
	if err != nil {
		return nil, err
	}

	soalRes := make([]SoalLatihanRes, len(latihan.SoalLatihan))
	for i, soal := range latihan.SoalLatihan {
		soalRes[i] = SoalLatihanRes{
			ID:        soal.ID.String(),
			LatihanID: soal.LatihanID.String(),
			Soal:      soal.Soal,
		}
	}

	res := LatihanByIdRes{
		ID:          latihan.ID.String(),
		Name:        latihan.Name,
		TotalSoal:   latihan.TotalSoal,
		SoalLatihan: soalRes,
	}

	return &res, nil
}

func (s *service) AddLatihan(ctx context.Context, req LatihanReq) error {
	db, err := s.dbSelector.GetDBByRole(ctx)
	if err != nil {
		return apierror.FromErr(err)
	}

	tx := db.Begin()
	if tx.Error != nil {
		return tx.Error
	}

	latihanID := uuid.New()

	latihan := Latihan{
		ID:        latihanID,
		Name:      req.Name,
		TotalSoal: len(req.SoalLatihan),
		CreatedAt: time.Now(),
		UpdatedAt: time.Now(),
	}

	if err := tx.WithContext(ctx).Create(&latihan).Error; err != nil {
		tx.Rollback()
		return apierror.FromErr(err)
	}

	soalList := make([]SoalLatihan, 0, len(req.SoalLatihan))
	for _, soal := range req.SoalLatihan {
		soalList = append(soalList, SoalLatihan{
			ID:        uuid.New(),
			LatihanID: latihanID,
			Soal:      strings.ToUpper(soal),
		})
	}

	if len(soalList) > 0 {
		if err := tx.WithContext(ctx).Create(&soalList).Error; err != nil {
			tx.Rollback()
			return apierror.FromErr(err)
		}
	}

	return tx.Commit().Error
}

func (s *service) DeleteLatihan(ctx context.Context, latihanId string) error {
	db, err := s.dbSelector.GetDBByRole(ctx)
	if err != nil {
		return apierror.FromErr(err)
	}

	var latihan Latihan
	if err := db.WithContext(ctx).First(&latihan, "id = ?", latihanId).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return apierror.LatihanNotFound(latihanId)
		}
		return apierror.FromErr(err)
	}

	if err := db.WithContext(ctx).Delete(&latihan).Error; err != nil {
		return apierror.FromErr(err)
	}

	return nil
}
