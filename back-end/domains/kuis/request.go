package kuis

type KuisReq struct {
	Name       string        `json:"name" form:"name" binding:"required"`
	BatasWaktu int           `json:"batas_waktu" form:"batas_waktu" binding:"required"`
	SoalKuis   []SoalKuisReq `json:"soal_kuis" form:"soal_kuis" binding:"required"`
}

type SoalKuisReq struct {
	Soal         string        `json:"soal" form:"soal" binding:"required"`
	JawabanBenar string        `json:"jawaban_benar" form:"jawaban_benar" binding:"required"`
	OpsiKuis     []OpsiKuisReq `json:"opsi_kuis" form:"opsi_kuis" binding:"required"`
}

type OpsiKuisReq struct {
	Label string `json:"label" form:"label" binding:"required"`
	Text  string `json:"text" form:"text" binding:"required"`
}

type DeleteKuisReq struct {
	ID string `json:"id" binding:"required"`
}

type GetAllKuisReq struct {
	Page  int64
	Limit int64
}

type StatsKuisReq struct {
	KuisID string `json:"kuis_id" binding:"required"`
	Score  int    `json:"score" binding:"required"`
}
