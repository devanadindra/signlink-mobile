package kuis

type KuisRes struct {
	ID         string `json:"id"`
	Name       string `json:"name"`
	TotalSoal  int    `json:"total_soal"`
	BatasWaktu int    `json:"batas_waktu"`
	IsDone     bool   `json:"is_done"`
}

type KuisByIdRes struct {
	ID          string       `json:"id"`
	Name        string       `json:"name"`
	TotalSoal   int          `json:"total_soal"`
	BatasWaktu  int          `json:"batas_waktu"`
	SoalLatihan []SoaKuisRes `json:"soal_kuis"`
}

type SoaKuisRes struct {
	ID           string        `json:"id"`
	ModulID      string        `json:"modul_id"`
	Soal         string        `json:"soal"`
	JawabanBenar string        `json:"jawaban_benar"`
	OpsiKuis     []OpsiKuisRes `json:"opsi_kuis"`
}

type OpsiKuisRes struct {
	ID     string `json:"id"`
	SoalID string `json:"soal_id"`
	Label  string `json:"label"`
	Text   string `json:"text"`
}

type StatsKuisRes struct {
	ID        string `json:"id"`
	ModulID   string `json:"modul_id"`
	KuisName  string `json:"kuis_name"`
	TotalSoal int    `json:"total_soal"`
	Score     int    `json:"score"`
}
