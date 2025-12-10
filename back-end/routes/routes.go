package routes

import (
	"net/http"

	"github.com/gin-gonic/gin"

	"github.com/devanadindraa/NTTH-Store/back-end/database"
	"github.com/devanadindraa/NTTH-Store/back-end/domains/kamus"
	"github.com/devanadindraa/NTTH-Store/back-end/domains/latihan"
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
	latihanHandler latihan.Handler,
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
		user.POST("/google", mw.GoogleAuth(), userHandler.GoogleAuth)
		user.GET("/verify-token", mw.JWT(constants.ADMIN, constants.CUSTOMER), userHandler.VerifyToken)
		user.POST("/logout", mw.JWT(constants.ADMIN, constants.CUSTOMER), userHandler.Logout)
		user.POST("/reset-req", mw.BasicAuth, userHandler.ResetPassword)
		user.PATCH("/reset-submit", mw.BasicAuth, userHandler.ResetPasswordSubmit)
		user.POST("/register", mw.BasicAuth, userHandler.Register)
		user.POST("/registerAdmin", mw.JWT(constants.ADMIN), userHandler.RegisterAdmin)
		user.PATCH("/updateUser", mw.JWT(constants.CUSTOMER, constants.ADMIN), userHandler.UpdateProfile)
		user.POST("/avatar", mw.JWT(constants.CUSTOMER, constants.ADMIN), userHandler.AddAvatar)
		user.DELETE("/avatar", mw.JWT(constants.CUSTOMER, constants.ADMIN), userHandler.DeleteAvatar)
		user.PATCH("/password", mw.JWT(constants.ADMIN, constants.CUSTOMER), userHandler.ChangePassword)
		user.GET("/get-personal", mw.JWT(constants.ADMIN, constants.CUSTOMER), userHandler.GetPersonal)
		user.GET("/check-jwt", mw.JWT(constants.ADMIN, constants.CUSTOMER), func(ctx *gin.Context) {
			respond.Success(ctx, http.StatusOK, "JWT is valid")
		})
	}

	kamus := api.Group("/kamus")
	{
		kamus.GET("/", mw.JWT(constants.ADMIN, constants.CUSTOMER), kamusHandler.GetKamus)
		kamus.GET("/all", mw.JWT(constants.ADMIN, constants.CUSTOMER), kamusHandler.GetAllKamus)
		kamus.POST("/", mw.JWT(constants.ADMIN), kamusHandler.AddKamus)
		kamus.DELETE("/:id", mw.JWT(constants.ADMIN), kamusHandler.DeleteKamus)
	}

	latihan := api.Group("/latihan")
	{
		latihan.GET("/", mw.JWT(constants.ADMIN, constants.CUSTOMER), latihanHandler.GetAllLatihan)
		latihan.GET("/:id", mw.JWT(constants.ADMIN, constants.CUSTOMER), latihanHandler.GetLatihanById)
		latihan.POST("/", mw.JWT(constants.ADMIN), latihanHandler.AddLatihan)
		latihan.POST("/stats", mw.JWT(constants.ADMIN, constants.CUSTOMER), latihanHandler.AddStatsLatihan)
		latihan.GET("/stats", mw.JWT(constants.ADMIN, constants.CUSTOMER), latihanHandler.GetStatsByUserId)
		latihan.DELETE("/:id", mw.JWT(constants.ADMIN), latihanHandler.DeleteLatihan)
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
