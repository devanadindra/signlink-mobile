package com.example.signlink.data.repository

import com.example.signlink.data.models.ApiResponse
import com.example.signlink.data.models.customer.AvatarRes
import com.example.signlink.data.models.customer.DeleteAvatarRes
import com.example.signlink.data.models.customer.PersonalRes
import com.example.signlink.data.models.customer.UpdateProfileReq
import com.example.signlink.data.services.CustomerService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class CustomerRepository(private val service: CustomerService) {
    suspend fun getPersonal(token: String): ApiResponse<PersonalRes>? {
        val response = service.getPersonal(token)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun updateProfile(token: String, name: String, email: String): ApiResponse<PersonalRes>? {
        val response = service.updateProfile(token, UpdateProfileReq(name, email))
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun deleteAvatar(token: String): ApiResponse<DeleteAvatarRes>? {
        val response = service.deleteAvatar(token)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun addAvatar(token: String, avatar: File): ApiResponse<AvatarRes>? {

        val mimeType = "image/*".toMediaTypeOrNull()
        val extension = when (mimeType.toString()) {
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            else -> "jpg"
        }

        val forcedName = "avatar_${System.currentTimeMillis()}.$extension"

        val requestBody = avatar.asRequestBody(mimeType)
        val imagePart = MultipartBody.Part.createFormData("avatar", forcedName, requestBody)

        val response = service.addAvatar(token, imagePart)
        return if (response.isSuccessful) response.body() else null
    }

}