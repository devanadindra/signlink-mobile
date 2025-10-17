package user

import (
	"time"

	"github.com/google/uuid"
)

type LoginRes struct {
	Token   string    `json:"token"`
	Expires time.Time `json:"expires"`
}

type VerifyTokenRes struct {
	TokenVerified bool `json:"tokenVerified"`
}

type LogoutRes struct {
	LoggedOut bool `json:"loggedOut"`
}

type ActivityRes struct {
	UserID      uuid.UUID
	Name        string
	Description string
	CreatedAt   time.Time
}

type PersonalRes struct {
	ID        uuid.UUID
	Name      string
	Password  string
	Email     string
	AvatarUrl string
	CreatedAt time.Time
	UpdatedAt time.Time
}

type ResetPasswordRes struct {
	Email string
}
