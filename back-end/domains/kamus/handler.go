package kamus

import (
	"errors"
	"net/http"

	apierror "github.com/devanadindraa/NTTH-Store/back-end/utils/api-error"
	"github.com/devanadindraa/NTTH-Store/back-end/utils/respond"
	"github.com/gin-gonic/gin"
	"github.com/go-playground/validator/v10"
)

type Handler interface {
	GetKamus(ctx *gin.Context)
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
		respond.Error(ctx, apierror.FromErr(errors.New("kategori tidak boleh kosong")))
		return
	}

	res, err := h.service.GetKamus(ctx, kategori)
	if err != nil {
		respond.Error(ctx, apierror.FromErr(err))
		return
	}

	respond.Success(ctx, http.StatusOK, res)
}
