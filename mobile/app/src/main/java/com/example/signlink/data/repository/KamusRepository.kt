package com.example.signlink.data.repository
import com.example.signlink.data.models.kamus.*
import com.example.signlink.data.services.KamusService
import javax.inject.Inject

class KamusRepository @Inject constructor(private val service: KamusService) {
    suspend fun getKamus(token: String, kategori: String): List<KamusData>? {
        val response = service.getKamus(token, kategori)
        return if (response.isSuccessful) response.body()?.data else null
    }
}