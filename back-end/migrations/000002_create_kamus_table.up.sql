CREATE TABLE
    IF NOT EXISTS kamus (
        id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
        arti VARCHAR NOT NULL UNIQUE,
        kategori VARCHAR NOT NULL,
        video_url VARCHAR,
        updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW (),
        created_at TIMESTAMPTZ NOT NULL DEFAULT NOW ()
    );

-- A
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'A', 'A', '/kamus_videos/A/A.mp4'),
    (gen_random_uuid(), 'Apa', 'A', '/kamus_videos/A/Apa.mp4'),
    (gen_random_uuid(), 'Apa_Kabar', 'A', '/kamus_videos/A/Apa_Kabar.mp4');

-- B
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'B', 'B', '/kamus_videos/B/B.mp4'),
    (gen_random_uuid(), 'Bagaimana', 'B', '/kamus_videos/B/Bagaimana.mp4'),
    (gen_random_uuid(), 'Baik', 'B', '/kamus_videos/B/Baik.mp4'),
    (gen_random_uuid(), 'Belajar', 'B', '/kamus_videos/B/Belajar.mp4'),
    (gen_random_uuid(), 'Berapa', 'B', '/kamus_videos/B/Berapa.mp4'),
    (gen_random_uuid(), 'Berdiri', 'B', '/kamus_videos/B/Berdiri.mp4'),
    (gen_random_uuid(), 'Bingung', 'B', '/kamus_videos/B/Bingung.mp4');

-- C
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'C', 'C', '/kamus_videos/C/C.mp4');

-- D
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'D', 'D', '/kamus_videos/D/D.mp4'),
    (gen_random_uuid(), 'Dia', 'D', '/kamus_videos/D/Dia.mp4'),
    (gen_random_uuid(), 'Dimana', 'D', '/kamus_videos/D/Dimana.mp4'),
    (gen_random_uuid(), 'Duduk', 'D', '/kamus_videos/D/Duduk.mp4');

-- E
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'E', 'E', '/kamus_videos/E/E.mp4');

-- F
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'F', 'F', '/kamus_videos/F/F.mp4');

-- G
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'G', 'G', '/kamus_videos/G/G.mp4');

-- H
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'H', 'H', '/kamus_videos/H/H.mp4'),
    (gen_random_uuid(), 'Halo', 'H', '/kamus_videos/H/Halo.mp4');

-- I
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'I', 'I', '/kamus_videos/I/I.mp4');

-- J
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'J', 'J', '/kamus_videos/J/J.mp4');

-- K
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'K', 'K', '/kamus_videos/K/K.mp4'),
    (gen_random_uuid(), 'Kalian', 'K', '/kamus_videos/K/Kalian.mp4'),
    (gen_random_uuid(), 'Kami', 'K', '/kamus_videos/K/Kami.mp4'),
    (gen_random_uuid(), 'Kamu', 'K', '/kamus_videos/K/Kamu.mp4'),
    (gen_random_uuid(), 'Kapan', 'K', '/kamus_videos/K/Kapan.mp4'),
    (gen_random_uuid(), 'Kemana', 'K', '/kamus_videos/K/Kemana.mp4'),
    (gen_random_uuid(), 'Kita', 'K', '/kamus_videos/K/Kita.mp4');

-- L
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'L', 'L', '/kamus_videos/L/L.mp4');

-- M
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'M', 'M', '/kamus_videos/M/M.mp4'),
    (gen_random_uuid(), 'Makan', 'M', '/kamus_videos/M/Makan.mp4'),
    (gen_random_uuid(), 'Mandi', 'M', '/kamus_videos/M/Mandi.mp4'),
    (gen_random_uuid(), 'Marah', 'M', '/kamus_videos/M/Marah.mp4'),
    (gen_random_uuid(), 'Melihat', 'M', '/kamus_videos/M/Melihat.mp4'),
    (gen_random_uuid(), 'Membaca', 'M', '/kamus_videos/M/Membaca.mp4'),
    (gen_random_uuid(), 'Menulis', 'M', '/kamus_videos/M/Menulis.mp4'),
    (gen_random_uuid(), 'Mereka', 'M', '/kamus_videos/M/Mereka.mp4'),
    (gen_random_uuid(), 'Minum', 'M', '/kamus_videos/M/Minum.mp4');

-- N
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'N', 'N', '/kamus_videos/N/N.mp4');

-- O
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'O', 'O', '/kamus_videos/O/O.mp4');

-- P
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'P', 'P', '/kamus_videos/P/P.mp4'),
    (gen_random_uuid(), 'Pendek', 'P', '/kamus_videos/P/Pendek.mp4');

-- Q
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'Q', 'Q', '/kamus_videos/Q/Q.mp4');

-- R
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'R', 'R', '/kamus_videos/R/R.mp4'),
    (gen_random_uuid(), 'Ramah', 'R', '/kamus_videos/R/Ramah.mp4');

-- S
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'S', 'S', '/kamus_videos/S/S.mp4'),
    (gen_random_uuid(), 'Sabar', 'S', '/kamus_videos/S/Sabar.mp4'),
    (gen_random_uuid(), 'Saya', 'S', '/kamus_videos/S/Saya.mp4'),
    (gen_random_uuid(), 'Sedih', 'S', '/kamus_videos/S/Sedih.mp4'),
    (gen_random_uuid(), 'Selamat_Malam', 'S', '/kamus_videos/S/Selamat_Malam.mp4'),
    (gen_random_uuid(), 'Selamat_Pagi', 'S', '/kamus_videos/S/Selamat_Pagi.mp4'),
    (gen_random_uuid(), 'Selamat_Siang', 'S', '/kamus_videos/S/Selamat_Siang.mp4'),
    (gen_random_uuid(), 'Selamat_Sore', 'S', '/kamus_videos/S/Selamat_Sore.mp4'),
    (gen_random_uuid(), 'Senang', 'S', '/kamus_videos/S/Senang.mp4'),
    (gen_random_uuid(), 'Siapa', 'S', '/kamus_videos/S/Siapa.mp4');

-- T
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'T', 'T', '/kamus_videos/T/T.mp4'),
    (gen_random_uuid(), 'Terima_Kasih', 'T', '/kamus_videos/T/Terima_Kasih.mp4'),
    (gen_random_uuid(), 'Tidur', 'T', '/kamus_videos/T/Tidur.mp4'),
    (gen_random_uuid(), 'Tinggi', 'T', '/kamus_videos/T/Tinggi.mp4');

-- U
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'U', 'U', '/kamus_videos/U/U.mp4');

-- V
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'V', 'V', '/kamus_videos/V/V.mp4');

-- W
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'W', 'W', '/kamus_videos/W/W.mp4');

-- X
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'X', 'X', '/kamus_videos/X/X.mp4');

-- Y
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'Y', 'Y', '/kamus_videos/Y/Y.mp4');

-- Z
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'Z', 'Z', '/kamus_videos/Z/Z.mp4');