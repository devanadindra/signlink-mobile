# =========================
# Stage 1: Builder
# =========================
FROM golang:latest-alpine AS builder

# Install git dan build tools
RUN apk add --no-cache git build-base

# Set working directory
WORKDIR /app

# Copy go.mod dan go.sum dulu (biar caching efektif)
COPY go.mod go.sum ./
RUN go mod download

# Copy source code
COPY . .

# Build executable
RUN go build -o app .

# =========================
# Stage 2: Final Image
# =========================
FROM alpine:latest

# Install ca-certificates (needed for HTTPS requests)
RUN apk add --no-cache ca-certificates

# Set working directory
WORKDIR /app

# Copy executable dari builder
COPY --from=builder /app/app .

# Expose backend port
EXPOSE 7777

# Run the app
CMD ["./app"]
