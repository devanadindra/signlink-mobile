package com.example.signlink.data.services

import com.example.signlink.data.models.ApiResponse
import com.example.signlink.data.models.PaginatedData
import com.example.signlink.data.models.kuis.AddKuisRes
import com.example.signlink.data.models.kuis.AddStatsKuisRes
import com.example.signlink.data.models.kuis.DeleteKuisRes
import com.example.signlink.data.models.kuis.KuisByIdRes
import com.example.signlink.data.models.kuis.KuisData
import com.example.signlink.data.models.kuis.KuisReq
import com.example.signlink.data.models.kuis.StatsKuisByUserIdRes
import com.example.signlink.data.models.kuis.StatsKuisReq
import retrofit2.Response
import retrofit2.http.*

interface KuisService {

    @GET("kuis/")
    suspend fun getAllKuis(
        @Header("Authorization") authHeader: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<PaginatedData<KuisData>>>

    @GET("kuis/{id}")
    suspend fun getKuisById(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): Response<ApiResponse<KuisByIdRes>>

    @POST("kuis/")
    suspend fun addKuis(
        @Header("Authorization") authHeader: String,
        @Body req: KuisReq
    ): Response<ApiResponse<AddKuisRes>>

    @POST("kuis/stats")
    suspend fun addStatsKuis(
        @Header("Authorization") authHeader: String,
        @Body req: StatsKuisReq
    ): Response<ApiResponse<AddStatsKuisRes>>

    @GET("kuis/stats")
    suspend fun getStatsKuisByUserId(
        @Header("Authorization") authHeader: String,
    ): Response<ApiResponse<List<StatsKuisByUserIdRes>>>

    @DELETE("kuis/{id}")
    suspend fun deleteKuis(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): Response<ApiResponse<DeleteKuisRes>>
}
