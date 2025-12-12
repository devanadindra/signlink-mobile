package kuis

import (
	"time"

	"github.com/google/uuid"
)

type ModulKuis struct {
	ID         uuid.UUID `gorm:"type:uuid;default:gen_random_uuid();primaryKey"`
	Name       string
	TotalSoal  int
	BatasWaktu int
	SoalKuis   []SoalKuis  `gorm:"foreignKey:ModulID;references:ID"`
	StatsKuis  []StatsKuis `gorm:"foreignKey:KuisID;references:ID"`
	CreatedAt  time.Time   `gorm:"autoCreateTime"`
	UpdatedAt  time.Time   `gorm:"autoUpdateTime"`
}

func (ModulKuis) TableName() string {
	return "modul_kuis"
}

type SoalKuis struct {
	ID           uuid.UUID  `gorm:"type:uuid;default:gen_random_uuid();primaryKey"`
	ModulID      uuid.UUID  `gorm:"type:uuid;not null"`
	VideoURL     string     `gorm:"type:text;not null"`
	Soal         string     `gorm:"type:text;not null"`
	JawabanBenar string     `gorm:"type:varchar(255);not null"`
	OpsiKuis     []OpsiKuis `gorm:"foreignKey:SoalID;references:ID"`
	CreatedAt    time.Time  `gorm:"autoCreateTime"`
	UpdatedAt    time.Time  `gorm:"autoUpdateTime"`
}

func (SoalKuis) TableName() string {
	return "soal_kuis"
}

type OpsiKuis struct {
	ID        uuid.UUID `gorm:"type:uuid;default:gen_random_uuid();primaryKey"`
	SoalID    uuid.UUID `gorm:"type:uuid;not null"`
	Label     string    `gorm:"type:varchar(5);not null"`
	Text      string    `gorm:"type:varchar(255);not null"`
	CreatedAt time.Time `gorm:"autoCreateTime"`
	UpdatedAt time.Time `gorm:"autoUpdateTime"`
}

func (OpsiKuis) TableName() string {
	return "opsi_kuis"
}

type StatsKuis struct {
	ID        uuid.UUID `gorm:"type:uuid;default:gen_random_uuid();primaryKey"`
	KuisID    uuid.UUID
	UserID    uuid.UUID
	Score     int       `gorm:"type:int;not null"`
	CreatedAt time.Time `gorm:"autoCreateTime"`
	UpdatedAt time.Time `gorm:"autoUpdateTime"`
}

func (StatsKuis) TableName() string {
	return "stats_kuis"
}
