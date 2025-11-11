package com.example.signlink.data.services

import com.example.signlink.data.models.ApiResponse
import com.example.signlink.data.models.kamus.AddKamusRes
import com.example.signlink.data.models.kamus.DeleteKamusRes
import com.example.signlink.data.models.kamus.KamusData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface KamusService {

    @GET("kamus/")
    suspend fun getKamus(
        @Header("Authorization") authHeader: String,
        @Query("kategori") kategori: String
    ): Response<ApiResponse<List<KamusData>>>

    @Multipart
    @POST("kamus/")
    suspend fun addKamus(
        @Header("Authorization") authHeader: String,
        @Part("arti") arti: RequestBody,
        @Part("kategori") kategori: RequestBody,
        @Part video: MultipartBody.Part
    ): Response<ApiResponse<AddKamusRes>>

    @DELETE("kamus/{id}")
    suspend fun deleteKamus(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): Response<ApiResponse<DeleteKamusRes>>
}
