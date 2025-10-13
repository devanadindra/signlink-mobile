CREATE TABLE
    IF NOT EXISTS kamus (
        id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
        arti VARCHAR NOT NULL,
        kategori VARCHAR NOT NULL,
        video_url VARCHAR,
        updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW (),
        created_at TIMESTAMPTZ NOT NULL DEFAULT NOW ()
    );

-- A
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'A', 'A', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/A.webm'),
    (gen_random_uuid(), 'Aba', 'A', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Aba.webm'),
    (gen_random_uuid(), 'Aba-aba', 'A', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Aba-aba.webm'),
    (gen_random_uuid(), 'Abang', 'A', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Abang.webm'),
    (gen_random_uuid(), 'Abad', 'A', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Abad.webm');

-- B
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'B', 'B', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/B.webm'),
    (gen_random_uuid(), 'Babi', 'B', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Babi.webm'),
    (gen_random_uuid(), 'Baca', 'B', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Baca.webm'),
    (gen_random_uuid(), 'Badan', 'B', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Badan.webm'),
    (gen_random_uuid(), 'Bagaimana', 'B', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Bagaimana.webm');

-- C
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'C', 'C', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/C.webm'),
    (gen_random_uuid(), 'Cabai', 'C', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Cabai.webm'),
    (gen_random_uuid(), 'Cabut', 'C', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Cabut.webm'),
    (gen_random_uuid(), 'Cacing', 'C', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Cacing.webm'),
    (gen_random_uuid(), 'Cahaya', 'C', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Cahaya.webm');

-- D
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'D', 'D', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/D.webm'),
    (gen_random_uuid(), 'Dada', 'D', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Dada.webm'),
    (gen_random_uuid(), 'Daerah', 'D', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Daerah.webm'),
    (gen_random_uuid(), 'Dadu', 'D', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Dadu.webm'),
    (gen_random_uuid(), 'Daging', 'D', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Daging.webm');

-- E
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'E', 'E', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/E.webm'),
    (gen_random_uuid(), 'Egois', 'E', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Egois.webm'),
    (gen_random_uuid(), 'Ejek', 'E', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Ejek.webm'),
    (gen_random_uuid(), 'Efektif', 'E', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Efektif.webm'),
    (gen_random_uuid(), 'Ekor', 'E', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Ekor.webm');

-- F
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'F', 'F', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/F.webm'),
    (gen_random_uuid(), 'Faedah', 'F', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Faedah.webm'),
    (gen_random_uuid(), 'Festival', 'F', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Festival.webm'),
    (gen_random_uuid(), 'Fakir', 'F', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Fakir.webm'),
    (gen_random_uuid(), 'Fajar', 'F', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Fajar.webm');

-- G
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'G', 'G', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/G.webm'),
    (gen_random_uuid(), 'Gabah', 'G', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Gabah.webm'),
    (gen_random_uuid(), 'Gabung', 'G', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Gabung.webm'),
    (gen_random_uuid(), 'Gadis', 'G', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Gadis.webm'),
    (gen_random_uuid(), 'Gagal', 'G', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Gagal.webm');

-- H
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'H', 'H', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/H.webm'),
    (gen_random_uuid(), 'Habis', 'H', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Habis.webm'),
    (gen_random_uuid(), 'Hadap', 'H', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Hadap.webm'),
    (gen_random_uuid(), 'Hadiah', 'H', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Hadiah.webm'),
    (gen_random_uuid(), 'Hadir', 'H', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Hadir.webm');

-- I
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'I', 'I', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/I.webm'),
    (gen_random_uuid(), 'Ia', 'I', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Ia.webm'),
    (gen_random_uuid(), 'Ialah', 'I', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Ialah.webm'),
    (gen_random_uuid(), 'Ibarat', 'I', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Ibarat.webm'),
    (gen_random_uuid(), 'Iblis', 'I', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Iblis.webm');

-- J
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'J', 'J', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/J.webm'),
    (gen_random_uuid(), 'Jadi', 'J', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Jadi.webm'),
    (gen_random_uuid(), 'Jadwal', 'J', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Jadwal.webm'),
    (gen_random_uuid(), 'Jagal', 'J', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Jagal.webm'),
    (gen_random_uuid(), 'Jago', 'J', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Jago.webm');

-- K
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'K', 'K', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/K.webm'),
    (gen_random_uuid(), 'Kabar', 'K', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Kabar.webm'),
    (gen_random_uuid(), 'Kabur', 'K', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Kabur.webm'),
    (gen_random_uuid(), 'Kaca', 'K', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Kaca.webm'),
    (gen_random_uuid(), 'Kacamata', 'K', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Kacamata.webm');

-- L
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'L', 'L', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/L.webm'),
    (gen_random_uuid(), 'Label', 'L', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Label.webm'),
    (gen_random_uuid(), 'Labuh', 'L', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Labuh.webm'),
    (gen_random_uuid(), 'Ladang', 'L', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Ladang.webm'),
    (gen_random_uuid(), 'Lagu', 'L', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Lagu.webm');

-- M
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'M', 'M', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/M.webm'),
    (gen_random_uuid(), 'Maaf', 'M', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Maaf.webm'),
    (gen_random_uuid(), 'Mabuk', 'M', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Mabuk.webm'),
    (gen_random_uuid(), 'Madu', 'M', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Madu.webm'),
    (gen_random_uuid(), 'Macam', 'M', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Macam.webm');

-- N
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'N', 'N', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/N.webm'),
    (gen_random_uuid(), 'Nabi', 'N', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Nabi.webm'),
    (gen_random_uuid(), 'Nada', 'N', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Nada.webm'),
    (gen_random_uuid(), 'Nafas', 'N', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Nafas.webm'),
    (gen_random_uuid(), 'Nadi', 'N', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Nadi.webm');

-- O
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'O', 'O', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/O.webm'),
    (gen_random_uuid(), 'Obat', 'O', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Obat.webm'),
    (gen_random_uuid(), 'Obeng', 'O', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Obeng.webm'),
    (gen_random_uuid(), 'Objek', 'O', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Objek.webm'),
    (gen_random_uuid(), 'Obor', 'O', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Obor.webm');

-- P
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'P', 'P', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/P.webm'),
    (gen_random_uuid(), 'Pabrik', 'P', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Pabrik.webm'),
    (gen_random_uuid(), 'Pacar', 'P', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Pacar.webm'),
    (gen_random_uuid(), 'Pada', 'P', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Pada.webm'),
    (gen_random_uuid(), 'Padahal', 'P', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Padahal.webm');

-- Q
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'Q', 'Q', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Q.webm'),
    (gen_random_uuid(), 'Qador', 'Q', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Qador.webm');

-- R
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'R', 'R', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/R.webm'),
    (gen_random_uuid(), 'Raba', 'R', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Raba.webm'),
    (gen_random_uuid(), 'Rabu', 'R', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Rabu.webm'),
    (gen_random_uuid(), 'Racun', 'R', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Racun.webm'),
    (gen_random_uuid(), 'Radiasi', 'R', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Radiasi.webm');

-- S
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'S', 'S', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/S.webm'),
    (gen_random_uuid(), 'Saat', 'S', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Saat.webm'),
    (gen_random_uuid(), 'Sabar', 'S', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Sabar.webm'),
    (gen_random_uuid(), 'Siapa', 'S', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Siapa.webm'),
    (gen_random_uuid(), 'Sabuk', 'S', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Sabuk.webm');

-- T
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'T', 'T', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/T.webm'),
    (gen_random_uuid(), 'Taat', 'T', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Taat.webm'),
    (gen_random_uuid(), 'Tabah', 'T', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Tabah.webm'),
    (gen_random_uuid(), 'Tabiat', 'T', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Tabiat.webm'),
    (gen_random_uuid(), 'Tabib', 'T', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Tabib.webm');

-- U
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'U', 'U', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/U.webm'),
    (gen_random_uuid(), 'Uang', 'U', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Uang.webm'),
    (gen_random_uuid(), 'Uap', 'U', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Uap.webm'),
    (gen_random_uuid(), 'Ubah', 'U', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Ubah.webm'),
    (gen_random_uuid(), 'Ubur-ubur', 'U', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Ubu-ubur.webm');

-- V
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'V', 'V', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/V.webm'),
    (gen_random_uuid(), 'Vaksinasi', 'V', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Vaksinasi.webm'),
    (gen_random_uuid(), 'Variasi', 'V', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Variasi.webm'),
    (gen_random_uuid(), 'Vas', 'V', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Vas.webm'),
    (gen_random_uuid(), 'Vertikal', 'V', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Vertikal.webm');

-- W
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'W', 'W', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/W.webm'),
    (gen_random_uuid(), 'Wabah', 'W', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Wabah.webm'),
    (gen_random_uuid(), 'Wacana', 'W', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Wacana.webm'),
    (gen_random_uuid(), 'Wadah', 'W', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Wadah.webm'),
    (gen_random_uuid(), 'Waduk', 'W', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Waduk.webm');

-- X
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'X', 'X', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/X.webm');

-- Y
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'Y', 'Y', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Y.webm'),
    (gen_random_uuid(), 'Ya', 'Y', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Ya.webm'),
    (gen_random_uuid(), 'Yaitu', 'Y', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Yaitu.webm'),
    (gen_random_uuid(), 'Yakin', 'Y', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Yakin.webm'),
    (gen_random_uuid(), 'Yakni', 'Y', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Yakni.webm');

-- Z
INSERT INTO kamus (id, arti, kategori, video_url)
VALUES
    (gen_random_uuid(), 'Z', 'Z', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Z.webm'),
    (gen_random_uuid(), 'Zaitun', 'Z', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Zaitun.webm'),
    (gen_random_uuid(), 'Zakat', 'Z', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Zakat.webm'),
    (gen_random_uuid(), 'Zalim', 'Z', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Zalim.webm'),
    (gen_random_uuid(), 'Zaman', 'Z', 'http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Zaman.webm');