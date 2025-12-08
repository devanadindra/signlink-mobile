package latihan

type LatihanRes struct {
	ID          string        `json:"id"`
	Name        string        `json:"name"`
	TotalSoal   string        `json:"total_soal"`
	SoalLatihan []SoalLatihan `json:"soal_latihan"`
}

type SoalLatihanRes struct {
	ID        string `json:"id"`
	LatihanID string `json:"latihan_id"`
	Soal      string `json:"soal"`
}
