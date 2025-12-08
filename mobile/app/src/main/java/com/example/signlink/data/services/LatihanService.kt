package com.example.signlink.data.services

import com.example.signlink.data.models.ApiResponse
import com.example.signlink.data.models.PaginatedData
import com.example.signlink.data.models.latihan.AddLatihanRes
import com.example.signlink.data.models.latihan.DeleteLatihanRes
import com.example.signlink.data.models.latihan.LatihanByIdRes
import com.example.signlink.data.models.latihan.LatihanData
import com.example.signlink.data.models.latihan.LatihanReq
import retrofit2.Response
import retrofit2.http.*

interface LatihanService {

    @GET("latihan/")
    suspend fun getAllLatihan(
        @Header("Authorization") authHeader: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<PaginatedData<LatihanData>>>

    @GET("latihan/{id}")
    suspend fun getLatihanById(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): Response<ApiResponse<LatihanByIdRes>>

    @Multipart
    @POST("latihan/")
    suspend fun addLatihan(
        @Header("Authorization") authHeader: String,
        @Body req: LatihanReq
    ): Response<ApiResponse<AddLatihanRes>>

    @DELETE("latihan/{id}")
    suspend fun deleteLatihan(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): Response<ApiResponse<DeleteLatihanRes>>
}
