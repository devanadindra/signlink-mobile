package com.example.signlink.data.models.kuis

import com.google.gson.annotations.SerializedName

data class KuisReq(
    val name: String,
    @SerializedName("batas_waktu")
    val batasWaktu: Int,
    @SerializedName("soal_kuis")
    val soalKuis: List<SoalKuisReq>
)

data class SoalKuisReq(
    val soal: String,
    @SerializedName("jawaban_benar")
    val jawabanBenar: String,
    @SerializedName("opsi_kuis")
    val opsiKuis: List<OpsiKuisReq>
)

data class OpsiKuisReq(
    val label: String,
    val text: String
)
data class StatsKuisReq(
    @SerializedName("kuis_id")
    val latihanId: String,
    val score: Int
)