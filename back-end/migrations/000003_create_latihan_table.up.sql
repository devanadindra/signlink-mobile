CREATE TABLE IF NOT EXISTS latihan (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    total_soal INT NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW (),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW ()
);

CREATE TABLE IF NOT EXISTS soal_latihan (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    latihan_id UUID NOT NULL REFERENCES latihan(id) ON DELETE CASCADE,
    soal TEXT NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW (),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW ()
)