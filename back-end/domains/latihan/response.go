package latihan

type LatihanRes struct {
	ID        string `json:"id"`
	Name      string `json:"name"`
	TotalSoal int    `json:"total_soal"`
	IsDone    bool   `json:"is_done"`
}

type LatihanByIdRes struct {
	ID          string           `json:"id"`
	Name        string           `json:"name"`
	TotalSoal   int              `json:"total_soal"`
	SoalLatihan []SoalLatihanRes `json:"soal_latihan"`
}

type SoalLatihanRes struct {
	ID        string `json:"id"`
	LatihanID string `json:"latihan_id"`
	Soal      string `json:"soal"`
}
