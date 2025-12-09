package com.example.signlink.data.models.latihan

import com.google.gson.annotations.SerializedName

data class LatihanData(
    val id: String,
    val name: String,
    @SerializedName("total_soal")
    val totalSoal: String,
)

data class LatihanByIdRes(
    val id: String,
    val name: String,
    @SerializedName("total_soal")
    val totalSoal: String,
    @SerializedName("soal_latihan")
    val soalLatihan: List<SoalLatihanRes>,
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