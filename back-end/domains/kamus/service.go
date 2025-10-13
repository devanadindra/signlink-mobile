package kamus

import (
	"context"

	"github.com/devanadindraa/NTTH-Store/back-end/database"
	"github.com/devanadindraa/NTTH-Store/back-end/utils/config"
	"github.com/devanadindraa/NTTH-Store/back-end/utils/dbselector"
)

type Service interface {
	GetKamus(ctx context.Context, kategori string) (*[]KamusRes, error)
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
