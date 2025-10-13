package logger

import (
	"time"

	"github.com/devanadindraa/NTTH-Store/back-end/utils/constants"
)

const PACKAGE_NAME = "github.com/devanadindraa/NTTH-Store/back-end"

type LogPayload struct {
	Method         string
	Path           string
	StatusCode     int
	Took           time.Duration
	RequestPayload *constants.RequestPayload
}

func Setdata(env, ver string) {
	environment = env
	version = ver
}

var (
	environment = "unknown"
	version     = "unknown"
)
