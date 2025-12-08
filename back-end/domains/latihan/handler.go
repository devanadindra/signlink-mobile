package latihan

import (
	"net/http"

	apierror "github.com/devanadindraa/NTTH-Store/back-end/utils/api-error"
	"github.com/devanadindraa/NTTH-Store/back-end/utils/common"
	"github.com/devanadindraa/NTTH-Store/back-end/utils/respond"
	"github.com/gin-gonic/gin"
	"github.com/go-playground/validator/v10"
)

type Handler interface {
	GetLatihanById(ctx *gin.Context)
	AddLatihan(ctx *gin.Context)
	DeleteLatihan(ctx *gin.Context)
	GetAllLatihan(ctx *gin.Context)
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

func (h *handler) GetLatihanById(ctx *gin.Context) {
	id := ctx.Param("id")

	res, err := h.service.GetLatihanById(ctx, id)
	if err != nil {
		respond.Error(ctx, apierror.FromErr(err))
		return
	}

	respond.Success(ctx, http.StatusOK, res)
}

func (h *handler) AddLatihan(ctx *gin.Context) {
	var req LatihanReq

	if err := ctx.ShouldBind(&req); err != nil {
		respond.Error(ctx, apierror.FromErr(err))
		return
	}

	if err := h.service.AddLatihan(ctx, req); err != nil {
		respond.Error(ctx, apierror.FromErr(err))
		return
	}

	respond.Success(ctx, http.StatusCreated, gin.H{"message": "latihan added successfully"})
}

func (h *handler) DeleteLatihan(ctx *gin.Context) {
	id := ctx.Param("id")

	if err := h.service.DeleteLatihan(ctx, id); err != nil {
		respond.Error(ctx, apierror.FromErr(err))
		return
	}

	respond.Success(ctx, http.StatusCreated, gin.H{"message": "latihan deleted successfully"})
}

func (h *handler) GetAllLatihan(ctx *gin.Context) {
	filter, err := common.GetMetaData(ctx, h.validate, "created_at", "name")
	if err != nil {
		respond.Error(ctx, apierror.FromErr(err))
		return
	}

	req := GetAllLatihanReq{
		Page:  filter.Page,
		Limit: filter.Limit,
	}

	latihanList, total, err := h.service.GetAllLatihan(ctx, req, *filter)
	if err != nil {
		respond.Error(ctx, apierror.FromErr(err))
		return
	}

	respond.Success(ctx, http.StatusOK, gin.H{
		"data":  latihanList,
		"total": total,
		"page":  filter.Page,
		"limit": filter.Limit,
	})
}
