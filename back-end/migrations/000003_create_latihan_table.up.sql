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
);

CREATE TABLE IF NOT EXISTS stats_latihan (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    latihan_id UUID NOT NULL REFERENCES latihan(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES customer(id) ON DELETE CASCADE,
    score FLOAT NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW (),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW ()
);

CREATE INDEX IF NOT EXISTS idx_soal_latihan ON soal_latihan(latihan_id);
CREATE INDEX IF NOT EXISTS idx_stats_latihan_user ON stats_latihan(user_id);
CREATE INDEX IF NOT EXISTS idx_stats_user_latihan
ON stats_latihan(user_id, latihan_id);

