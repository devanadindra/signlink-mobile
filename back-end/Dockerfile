# =========================
# Stage 1: Builder
# =========================
FROM golang:1.24-alpine AS builder

# Build deps
RUN apk add --no-cache git build-base

WORKDIR /app

# Enable go build cache
ENV GOMODCACHE=/go/pkg/mod
ENV GOCACHE=/root/.cache/go-build

# Copy go.mod & go.sum
COPY go.mod go.sum ./

RUN go mod download

# Copy source code
COPY . .

# Use caching mount for fast builds
RUN --mount=type=cache,target=/root/.cache/go-build \
    --mount=type=cache,target=/go/pkg/mod \
    go build -o app .

# =========================
# Stage 2: Final Image
# =========================
FROM alpine:latest

RUN apk add ca-certificates

WORKDIR /app

COPY --from=builder /app/app .

EXPOSE 7777

CMD ["./app"]
