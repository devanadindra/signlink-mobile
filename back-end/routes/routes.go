package routes

import (
	"net/http"

	"github.com/gin-gonic/gin"

	"github.com/devanadindraa/NTTH-Store/back-end/database"
	"github.com/devanadindraa/NTTH-Store/back-end/domains/kamus"
	"github.com/devanadindraa/NTTH-Store/back-end/domains/user"
	"github.com/devanadindraa/NTTH-Store/back-end/middlewares"
	apierror "github.com/devanadindraa/NTTH-Store/back-end/utils/api-error"
	"github.com/devanadindraa/NTTH-Store/back-end/utils/config"
	"github.com/devanadindraa/NTTH-Store/back-end/utils/constants"
	"github.com/devanadindraa/NTTH-Store/back-end/utils/respond"
)

func NewDependency(
	conf *config.Config,
	mw middlewares.Middlewares,
	adminDB *database.AdminDB,
	customerDB *database.CustomerDB,
	userHandler user.Handler,
	kamusHandler kamus.Handler,
) *Dependency {

	if conf.Environment != config.DEVELOPMENT_ENVIRONMENT {
		gin.SetMode(gin.ReleaseMode)
	}

	router := gin.New()
	router.HandleMethodNotAllowed = true
	router.ContextWithFallback = true

	// middleware
	{
		router.Use(mw.AddRequestId)
		router.Use(mw.Logging)
		router.Use(mw.RateLimiter)
		router.Use(mw.Recover)
	}

	api := router.Group("/api")
	api.Static("/uploads", "./uploads")
	api.Static("/kamus_videos", "./kamus_videos")
	api.GET("/health-check", HealthCheck)

	// domain user
	user := api.Group("/user")
	{
		user.POST("/login", mw.BasicAuth, userHandler.Login)
		user.GET("/verify-token", mw.JWT(constants.ADMIN, constants.CUSTOMER), userHandler.VerifyToken)
		user.POST("/logout", mw.JWT(constants.ADMIN, constants.CUSTOMER), userHandler.Logout)
		user.POST("/reset-req", mw.BasicAuth, userHandler.ResetPassword)
		user.PATCH("/reset-submit", mw.BasicAuth, userHandler.ResetPasswordSubmit)
		user.POST("/register", mw.BasicAuth, userHandler.Register)
		user.POST("/registerAdmin", mw.JWT(constants.ADMIN), userHandler.RegisterAdmin)
		user.PATCH("/updateUser", mw.JWT(constants.CUSTOMER, constants.ADMIN), userHandler.UpdateProfile)
		user.POST("/avatar", mw.JWT(constants.CUSTOMER, constants.ADMIN), userHandler.AddAvatar)
		user.PATCH("/password", mw.JWT(constants.ADMIN, constants.CUSTOMER), userHandler.ChangePassword)
		user.GET("/get-personal", mw.JWT(constants.ADMIN, constants.CUSTOMER), userHandler.GetPersonal)
		user.GET("/adminLog", mw.JWT(constants.ADMIN), userHandler.GetAdminActivity)
		user.GET("/check-jwt", mw.JWT(constants.ADMIN, constants.CUSTOMER), func(ctx *gin.Context) {
			respond.Success(ctx, http.StatusOK, "JWT is valid")
		})
	}

	kamus := api.Group("/kamus")
	{
		kamus.GET("/", mw.JWT(constants.ADMIN, constants.CUSTOMER), kamusHandler.GetKamus)
	}

	router.NoRoute(func(ctx *gin.Context) {
		respond.Error(ctx, apierror.NewWarn(http.StatusNotFound, "Page not found"))
	})

	router.NoMethod(func(ctx *gin.Context) {
		respond.Error(ctx, apierror.NewWarn(http.StatusMethodNotAllowed, "Method not allowed"))
	})

	return &Dependency{
		handler:    router,
		AdminDB:    adminDB,
		CustomerDB: customerDB,
	}
}

func HealthCheck(ctx *gin.Context) {
	respond.Success(ctx, http.StatusOK, "server running properly")
}
