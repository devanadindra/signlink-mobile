package com.example.signlink.data.services

import com.example.signlink.data.models.ApiResponse
import com.example.signlink.data.models.kamus.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface KamusService {
    @GET("kamus/")
    suspend fun getKamus(
        @Header("Authorization") authHeader: String,
        @retrofit2.http.Query("kategori") kategori: String
    ): Response<ApiResponse<List<KamusData>>>
}
