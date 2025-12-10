package latihan

import (
	"time"

	"github.com/google/uuid"
)

type Latihan struct {
	ID           uuid.UUID `gorm:"type:uuid;default:gen_random_uuid();primaryKey"`
	Name         string
	TotalSoal    int
	SoalLatihan  []SoalLatihan  `gorm:"foreignKey:LatihanID;references:ID"`
	StatsLatihan []StatsLatihan `gorm:"foreignKey:LatihanID;references:ID"`
	CreatedAt    time.Time      `gorm:"autoCreateTime"`
	UpdatedAt    time.Time      `gorm:"autoUpdateTime"`
}

func (Latihan) TableName() string {
	return "latihan"
}

type SoalLatihan struct {
	ID        uuid.UUID `gorm:"type:uuid;default:gen_random_uuid();primaryKey"`
	LatihanID uuid.UUID `gorm:"type:uuid;not null"`
	Soal      string    `gorm:"type:text;not null"`
	CreatedAt time.Time `gorm:"autoCreateTime"`
	UpdatedAt time.Time `gorm:"autoUpdateTime"`
}

func (SoalLatihan) TableName() string {
	return "soal_latihan"
}

type StatsLatihan struct {
	ID        uuid.UUID `gorm:"type:uuid;default:gen_random_uuid();primaryKey"`
	LatihanID uuid.UUID
	UserID    uuid.UUID
	Score     float64   `gorm:"type:float64;not null"`
	CreatedAt time.Time `gorm:"autoCreateTime"`
	UpdatedAt time.Time `gorm:"autoUpdateTime"`
}

func (StatsLatihan) TableName() string {
	return "stats_latihan"
}
