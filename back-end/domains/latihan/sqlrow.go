package latihan

import (
	"time"

	"github.com/google/uuid"
)

type Latihan struct {
	ID        uuid.UUID `gorm:"type:uuid;default:gen_random_uuid();primaryKey"`
	name      string
	TotalSoal int
	CreatedAt time.Time `gorm:"autoCreateTime"`
	UpdatedAt time.Time `gorm:"autoUpdateTime"`
}

func (Latihan) TableName() string {
	return "latihan"
}

type SoalLatihan struct {
	ID        uuid.UUID `gorm:"type:uuid;default:gen_random_uuid();primaryKey"`
	LatihanID uuid.UUID `gorm:"type:uuid"`
	soal      string
	CreatedAt time.Time `gorm:"autoCreateTime"`
	UpdatedAt time.Time `gorm:"autoUpdateTime"`
}

func (SoalLatihan) TableName() string {
	return "soal_latihan"
}
