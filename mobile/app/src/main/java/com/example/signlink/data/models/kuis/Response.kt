package com.example.signlink.data.models.kuis

import com.google.gson.annotations.SerializedName

data class KuisData(
    val id: String,
    val name: String,
    @SerializedName("total_soal")
    val totalSoal: String,
    @SerializedName("batas_waktu")
    val batasWaktu: Int,
    @SerializedName("is_done")
    val isDone: Boolean,
)

data class KuisByIdRes(
    val id: String,
    val name: String,
    @SerializedName("total_soal")
    val totalSoal: Int,
    @SerializedName("batas_waktu")
    val batasWaktu: Int,
    @SerializedName("soal_kuis")
    val soalKuis: List<SoalKuisRes>,
)

data class SoalKuisRes(
    val id: String,
    @SerializedName("modul_id")
    val modulId: String,
    @SerializedName("video_url")
    val videoUrl: String,
    val soal: String,
    @SerializedName("jawaban_benar")
    val jawabanBenar: String,
    @SerializedName("opsi_kuis")
    val opsiKuis: List<OpsiKuisRes>,
)

data class OpsiKuisRes(
    val id: String,
    @SerializedName("soal_id")
    val soalId: String,
    val label: String,
    val text: String
)

data class StatsKuisByUserIdRes(
    val id: String,
    @SerializedName("kuis_id")
    val kuisId: String,
    @SerializedName("kuis_name")
    val kuisName: String,
    @SerializedName("total_soal")
    val totalSoal: Int,
    val score: Int,
)

data class AddKuisRes(
    val message: String
)

data class AddStatsKuisRes(
    val message: String
)

data class DeleteKuisRes(
    val message: String
)