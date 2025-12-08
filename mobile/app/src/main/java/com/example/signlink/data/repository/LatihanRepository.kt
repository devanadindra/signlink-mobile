package com.example.signlink.data.repository
import com.example.signlink.data.models.ApiResponse
import com.example.signlink.data.models.latihan.AddLatihanRes
import com.example.signlink.data.models.latihan.DeleteLatihanRes
import com.example.signlink.data.models.latihan.LatihanByIdRes
import com.example.signlink.data.models.latihan.LatihanData
import com.example.signlink.data.models.latihan.LatihanReq
import com.example.signlink.data.services.LatihanService
import retrofit2.Response
import javax.inject.Inject


class LatihanRepository @Inject constructor(private val service: LatihanService) {
    suspend fun getLatihanById(token: String, latihanId: String): LatihanByIdRes? {
        val response = service.getLatihanById(token, latihanId)
        return if (response.isSuccessful) response.body()?.data else null
    }

    suspend fun getAllLatihan(
        token: String,
        page: Int,
        limit: Int
    ): List<LatihanData>? {

        val response = service.getAllLatihan(
            authHeader = token,
            page = page,
            limit = limit
        )

        return if (response.isSuccessful)
            response.body()?.data?.data
        else null
    }

    suspend fun addLatihan(token: String, req: LatihanReq): Response<ApiResponse<AddLatihanRes>> {

        return service.addLatihan(
            authHeader = token,
            req = req,
        )
    }

    suspend fun deleteLatihan(token: String, latihanId: String): Response<ApiResponse<DeleteLatihanRes>>{
        return service.deleteLatihan(token, latihanId)
    }
}