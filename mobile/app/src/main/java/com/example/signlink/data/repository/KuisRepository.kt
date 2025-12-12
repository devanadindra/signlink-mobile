package com.example.signlink.data.repository
import com.example.signlink.data.models.ApiResponse
import com.example.signlink.data.models.kuis.AddKuisRes
import com.example.signlink.data.models.kuis.AddStatsKuisRes
import com.example.signlink.data.models.kuis.DeleteKuisRes
import com.example.signlink.data.models.kuis.KuisByIdRes
import com.example.signlink.data.models.kuis.KuisData
import com.example.signlink.data.models.kuis.KuisReq
import com.example.signlink.data.models.kuis.StatsKuisByUserIdRes
import com.example.signlink.data.models.kuis.StatsKuisReq
import com.example.signlink.data.services.KuisService
import retrofit2.Response
import javax.inject.Inject


class KuisRepository @Inject constructor(private val service: KuisService) {
    suspend fun getKuisById(token: String, kuisId: String): KuisByIdRes? {
        val response = service.getKuisById(token, kuisId)
        return if (response.isSuccessful) response.body()?.data else null
    }

    suspend fun getAllKuis(
        token: String,
        page: Int,
        limit: Int
    ): List<KuisData>? {

        val response = service.getAllKuis(
            authHeader = token,
            page = page,
            limit = limit
        )

        return if (response.isSuccessful)
            response.body()?.data?.data
        else null
    }

    suspend fun addKuis(token: String, req: KuisReq): Response<ApiResponse<AddKuisRes>> {

        return service.addKuis(
            authHeader = token,
            req = req,
        )
    }

    suspend fun addStatsKuis(token: String, req: StatsKuisReq): Response<ApiResponse<AddStatsKuisRes>> {

        return service.addStatsKuis(
            authHeader = token,
            req = req,
        )
    }

    suspend fun deleteKuis(token: String, kuisId: String): Response<ApiResponse<DeleteKuisRes>>{
        return service.deleteKuis(token, kuisId)
    }

    suspend fun getStatsKuisByUserId(
        token: String,
    ): List<StatsKuisByUserIdRes>? {

        val response = service.getStatsKuisByUserId(
            authHeader = token
        )

        return (if (response.isSuccessful)
            response.body()?.data
        else null)
    }
}