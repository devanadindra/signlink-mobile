package kamus

import (
	"net/http"
	"strings"

	apierror "github.com/devanadindra/signlink-mobile/back-end/utils/api-error"
	"github.com/devanadindra/signlink-mobile/back-end/utils/common"
	"github.com/devanadindra/signlink-mobile/back-end/utils/respond"
	"github.com/gin-gonic/gin"
	"github.com/go-playground/validator/v10"
)

type Handler interface {
	GetKamus(ctx *gin.Context)
	AddKamus(ctx *gin.Context)
	DeleteKamus(ctx *gin.Context)
	GetAllKamus(ctx *gin.Context)
	GetKamusByArti(ctx *gin.Context)
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

func (h *handler) GetKamus(ctx *gin.Context) {
	kategori := ctx.Query("kategori")
	if kategori == "" {
		respond.Error(ctx, apierror.InvalidCategory())
		return
	}

	res, err := h.service.GetKamus(ctx, kategori)
	if err != nil {
		respond.Error(ctx, apierror.FromErr(err))
		return
	}

	respond.Success(ctx, http.StatusOK, res)
}

func (h *handler) AddKamus(ctx *gin.Context) {
	var req KamusReq

	if err := ctx.ShouldBind(&req); err != nil {
		respond.Error(ctx, apierror.FromErr(err))
		return
	}

	if err := h.service.AddKamus(ctx, req); err != nil {
		if strings.Contains(err.Error(), "kamus_arti_key") {
			respond.Error(ctx, apierror.DuplicateArti(req.Arti))
			return
		}

		respond.Error(ctx, apierror.FromErr(err))
		return
	}

	respond.Success(ctx, http.StatusCreated, gin.H{"message": "kamus and video added successfully"})
}

func (h *handler) DeleteKamus(ctx *gin.Context) {
	id := ctx.Param("id")

	if err := h.service.DeleteKamus(ctx, id); err != nil {
		respond.Error(ctx, apierror.FromErr(err))
		return
	}

	respond.Success(ctx, http.StatusCreated, gin.H{"message": "kamus and video deleted successfully"})
}

func (h *handler) GetAllKamus(ctx *gin.Context) {
	filter, err := common.GetMetaData(ctx, h.validate, "created_at", "arti", "kategori")
	if err != nil {
		respond.Error(ctx, apierror.FromErr(err))
		return
	}

	input := GetAllKamusReq{
		Keyword: ctx.Query("keyword"),
	}

	kamusList, total, err := h.service.GetAllKamus(ctx, input, *filter)
	if err != nil {
		respond.Error(ctx, apierror.FromErr(err))
		return
	}

	respond.Success(ctx, http.StatusOK, gin.H{
		"data":  kamusList,
		"total": total,
		"page":  filter.Page,
		"limit": filter.Limit,
	})
}

func (h *handler) GetKamusByArti(ctx *gin.Context) {
	arti := ctx.Param("arti")

	kamus, err := h.service.GetKamusByArti(ctx, arti)
	if err != nil {
		respond.Error(ctx, apierror.FromErr(err))
		return
	}

	respond.Success(ctx, http.StatusCreated, kamus)
}
