package latihan

type LatihanReq struct {
	Name        string   `json:"name" form:"name" binding:"required"`
	SoalLatihan []string `json:"soal_latihan" form:"soal_latihan" binding:"required"`
}

type DeleteLatihanReq struct {
	ID string `json:"id" binding:"required"`
}

type GetAllLatihanReq struct {
	Page  int64
	Limit int64
}

type StatsLatihanReq struct {
	LatihanID string  `json:"latihan_id" binding:"required"`
	Score     float64 `json:"score" binding:"required"`
}
