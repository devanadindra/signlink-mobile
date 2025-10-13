package com.example.signlink.data.models.kamus

data class KamusResponse(
    val data: List<KamusData>?,
    val errors: Any?
)

data class KamusData(
    val id: String,
    val arti: String,
    val kategori: String,
    val url: String,
)