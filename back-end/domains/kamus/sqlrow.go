package kamus

import (
	"time"

	"github.com/google/uuid"
)

type Kamus struct {
	ID        uuid.UUID `gorm:"type:uuid;default:gen_random_uuid();primaryKey"`
	Arti      string    `gorm:"unique"`
	Kategori  string
	VideoUrl  string
	CreatedAt time.Time `gorm:"autoCreateTime"`
	UpdatedAt time.Time `gorm:"autoUpdateTime"`
}

func (Kamus) TableName() string {
	return "kamus"
}
