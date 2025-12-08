package latihan

type LatihanReq struct {
	Name        string           `form:"name" binding:"required"`
	SoalLatihan []SoalLatihanReq `form:"soal_latihan" binding:"required"`
}

type SoalLatihanReq struct {
	Soal string `form:"soal" binding:"required"`
}

type DeleteLatihanReq struct {
	ID string `json:"id" binding:"required"`
}

type GetAllKamusReq struct {
	Keyword string
	Page    int
	Limit   int
}
