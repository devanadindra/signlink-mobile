package kuis

import (
	"net/http"

	apierror "github.com/devanadindraa/NTTH-Store/back-end/utils/api-error"
	"github.com/devanadindraa/NTTH-Store/back-end/utils/common"
	"github.com/devanadindraa/NTTH-Store/back-end/utils/respond"
	"github.com/gin-gonic/gin"
	"github.com/go-playground/validator/v10"
)

type Handler interface {
	GetKuisById(ctx *gin.Context)
	AddKuis(ctx *gin.Context)
	DeleteKuis(ctx *gin.Context)
	GetAllKuis(ctx *gin.Context)
	AddStatsKuis(ctx *gin.Context)
	GetStatsByUserId(ctx *gin.Context)
}

type handler struct {
	service  Service
	validate *validator.Validate
}

func NewHandler(service Service, validate *validator.Validate) Handler {
	return &handler{
		service:  service,
		validate: validate,
	}
}

func (h *handler) GetKuisById(ctx *gin.Context) {
	id := ctx.Param("id")

	res, err := h.service.GetKuisById(ctx, id)
	if err != nil {
		respond.Error(ctx, apierror.FromErr(err))
		return
	}

	respond.Success(ctx, http.StatusOK, res)
}

func (h *handler) AddKuis(ctx *gin.Context) {
	var req KuisReq

	if err := ctx.ShouldBind(&req); err != nil {
		respond.Error(ctx, apierror.FromErr(err))
		return
	}

	if err := h.service.AddKuis(ctx, req); err != nil {
		respond.Error(ctx, apierror.FromErr(err))
		return
	}

	respond.Success(ctx, http.StatusCreated, gin.H{"message": "kuis added successfully"})
}

func (h *handler) DeleteKuis(ctx *gin.Context) {
	id := ctx.Param("id")

	if err := h.service.DeleteKuis(ctx, id); err != nil {
		respond.Error(ctx, apierror.FromErr(err))
		return
	}

	respond.Success(ctx, http.StatusCreated, gin.H{"message": "kuis deleted successfully"})
}

func (h *handler) GetAllKuis(ctx *gin.Context) {
	filter, err := common.GetMetaData(ctx, h.validate, "created_at", "name")
	if err != nil {
		respond.Error(ctx, apierror.FromErr(err))
		return
	}

	req := GetAllKuisReq{
		Page:  filter.Page,
		Limit: filter.Limit,
	}

	kuisList, total, err := h.service.GetAllKuis(ctx, req, *filter)
	if err != nil {
		respond.Error(ctx, apierror.FromErr(err))
		return
	}

	respond.Success(ctx, http.StatusOK, gin.H{
		"data":  kuisList,
		"total": total,
		"page":  filter.Page,
		"limit": filter.Limit,
	})
}

func (h *handler) AddStatsKuis(ctx *gin.Context) {
	var req StatsKuisReq

	if err := ctx.ShouldBind(&req); err != nil {
		respond.Error(ctx, apierror.FromErr(err))
		return
	}

	if err := h.service.AddStatsKuis(ctx, req); err != nil {
		respond.Error(ctx, apierror.FromErr(err))
		return
	}

	respond.Success(ctx, http.StatusCreated, gin.H{"message": "stats latihan added successfully"})
}

func (h *handler) GetStatsByUserId(ctx *gin.Context) {

	res, err := h.service.GetStatsByUserId(ctx)
	if err != nil {
		respond.Error(ctx, apierror.FromErr(err))
		return
	}

	respond.Success(ctx, http.StatusOK, res)
}
