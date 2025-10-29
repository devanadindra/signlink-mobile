package com.example.signlink.data.models.kamus

import java.io.File

data class KamusReq(
    val arti: String,
    val kategori: String,
    val video: File
)