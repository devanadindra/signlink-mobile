package com.example.signlink.data.repository
import com.example.signlink.data.models.ApiResponse
import com.example.signlink.data.models.kamus.AddKamusRes
import com.example.signlink.data.models.kamus.DeleteKamusRes
import com.example.signlink.data.models.kamus.DeleteReq
import com.example.signlink.data.models.kamus.KamusData
import com.example.signlink.data.models.kamus.KamusReq
import com.example.signlink.data.services.KamusService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import javax.inject.Inject


class KamusRepository @Inject constructor(private val service: KamusService) {
    suspend fun getKamus(token: String, kategori: String): List<KamusData>? {
        val response = service.getKamus(token, kategori)
        return if (response.isSuccessful) response.body()?.data else null
    }

    suspend fun addKamus(token: String, req: KamusReq): Response<ApiResponse<AddKamusRes>> {
        val artiBody = req.arti.toRequestBody("text/plain".toMediaTypeOrNull())
        val kategoriBody = req.kategori.toRequestBody("text/plain".toMediaTypeOrNull())
        val videoBody = req.video.asRequestBody("video/*".toMediaTypeOrNull())
        val videoPart = MultipartBody.Part.createFormData("video", req.video.name, videoBody)

        return service.addKamus(
            authHeader = token,
            arti = artiBody,
            kategori = kategoriBody,
            video = videoPart
        )
    }

    suspend fun deleteKamus(token: String, id: String): Response<ApiResponse<DeleteKamusRes>>{
        return service.deleteKamus(token, id)
    }
}