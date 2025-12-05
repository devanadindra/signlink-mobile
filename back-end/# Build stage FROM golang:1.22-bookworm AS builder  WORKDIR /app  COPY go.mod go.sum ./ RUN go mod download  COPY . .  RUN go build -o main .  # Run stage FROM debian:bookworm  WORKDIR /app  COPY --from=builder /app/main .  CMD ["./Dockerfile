# Build stage
FROM golang:1.22-bookworm AS builder

WORKDIR /app

COPY go.mod go.sum ./
RUN go mod download

COPY . .

RUN go build -o main .

# Run stage
FROM debian:bookworm

WORKDIR /app

COPY --from=builder /app/main .

CMD ["./main"]
