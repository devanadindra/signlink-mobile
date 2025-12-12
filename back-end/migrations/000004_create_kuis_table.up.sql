CREATE TABLE IF NOT EXISTS modul_kuis (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    total_soal INT NOT NULL,
    batas_waktu INT NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS soal_kuis (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    modul_id UUID NOT NULL REFERENCES modul_kuis(id) ON DELETE CASCADE,
    video_url TEXT NOT NULL,
    soal TEXT NOT NULL,
    jawaban_benar VARCHAR(255) NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS opsi_kuis (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    soal_id UUID NOT NULL REFERENCES soal_kuis(id) ON DELETE CASCADE,
    label VARCHAR(5) NOT NULL,
    text VARCHAR(255) NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS stats_kuis (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    kuis_id UUID NOT NULL REFERENCES modul_kuis(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES customer(id) ON DELETE CASCADE,
    score INT NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW (),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW ()
);

CREATE INDEX IF NOT EXISTS idx_soal_modul ON soal_kuis(modul_id);
CREATE INDEX IF NOT EXISTS idx_opsi_soal ON opsi_kuis(soal_id);
CREATE INDEX IF NOT EXISTS idx_stats_kuis_user ON stats_kuis(user_id);
CREATE INDEX IF NOT EXISTS idx_stats_user_kuis
ON stats_kuis(user_id, kuis_id);
