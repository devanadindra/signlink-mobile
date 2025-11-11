//go:build wireinject
// +build wireinject

package wireinject

import (
	"github.com/go-playground/validator/v10"
	_ "github.com/google/subcommands"
	"github.com/google/wire"

	"github.com/devanadindraa/NTTH-Store/back-end/database"
	"github.com/devanadindraa/NTTH-Store/back-end/domains/kamus"
	"github.com/devanadindraa/NTTH-Store/back-end/domains/user"
	"github.com/devanadindraa/NTTH-Store/back-end/middlewares"
	"github.com/devanadindraa/NTTH-Store/back-end/routes"
	"github.com/devanadindraa/NTTH-Store/back-end/utils/config"
	"github.com/devanadindraa/NTTH-Store/back-end/utils/dbselector"
)

var dbSet = wire.NewSet(
	database.NewDBCustomer,
	database.NewDBAdmin,
)

var dbSelectorSet = wire.NewSet(
	dbselector.NewDBService,
)

var userSet = wire.NewSet(
	user.NewService,
	user.NewHandler,
)

var kamusSet = wire.NewSet(
	kamus.NewService,
	kamus.NewHandler,
)

func initializeDependency(config *config.Config) (*routes.Dependency, error) {

	wire.Build(
		dbSet,
		dbSelectorSet,
		validator.New,
		middlewares.NewMiddlewares,
		routes.NewDependency,
		userSet,
		kamusSet,
	)

	return nil, nil
}
