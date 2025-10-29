package com.example.signlink.data.models.kamus

data class KamusData(
    val id: String,
    val arti: String,
    val kategori: String,
    val url: String,
)

data class AddKamusRes(
    val message: String
)