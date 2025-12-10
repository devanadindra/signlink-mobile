package com.example.signlink.data.models.latihan

import com.google.gson.annotations.SerializedName

data class LatihanData(
    val id: String,
    val name: String,
    @SerializedName("total_soal")
    val totalSoal: String,
    @SerializedName("is_done")
    val isDone: Boolean,
)

data class LatihanByIdRes(
    val id: String,
    val name: String,
    @SerializedName("total_soal")
    val totalSoal: String,
    @SerializedName("soal_latihan")
    val soalLatihan: List<SoalLatihanRes>,
)

data class StatsLatihanByUserIdRes(
    val id: String,
    @SerializedName("latihan_id")
    val latihanId: String,
    @SerializedName("latihan_name")
    val latihanName: String,
    @SerializedName("total_soal")
    val totalSoal: Int,
    val score: Float,
)

data class SoalLatihanRes(
    val id: String,
    val latihanId: String,
    val soal: String
)

data class AddLatihanRes(
    val message: String
)

data class AddStatsLatihanRes(
    val message: String
)

data class DeleteLatihanRes(
    val message: String
)