package com.example.signlink.data.models.latihan

import com.google.gson.annotations.SerializedName

data class LatihanReq(
    val name: String,
    @SerializedName("soal_latihan")
    val soalLatihan: List<String>
)

data class StatsLatihanReq(
    @SerializedName("latihan_id")
    val latihanId: String,
    val score: Float
)