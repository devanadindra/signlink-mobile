package com.example.signlink.data.models.latihan

import com.google.gson.annotations.SerializedName
import java.io.File

data class LatihanReq(
    val name: String,
    @SerializedName("soal_latihan")
    val soalLatihan: List<String>
)