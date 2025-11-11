package com.example.signlink.data.repository

import com.example.signlink.data.models.ApiResponse
import com.example.signlink.data.models.customer.LoginReq
import com.example.signlink.data.models.customer.LogoutRes
import com.example.signlink.data.models.customer.RegisterReq
import com.example.signlink.data.models.customer.ResetPasswordReq
import com.example.signlink.data.models.customer.ResetPasswordRes
import com.example.signlink.data.models.customer.ResetPasswordSubmit
import com.example.signlink.data.models.customer.ResetPasswordSubmitRes
import com.example.signlink.data.services.CustomerService
import com.example.signlink.data.utils.AuthUtil

class AuthRepository(private val service: CustomerService) {
    suspend fun login(role: String, email: String, password: String) =
        service.login(AuthUtil.basicAuth(), LoginReq(role, email, password))

    suspend fun register(name: String, email: String, password: String) =
        service.register(AuthUtil.basicAuth(), RegisterReq(name, email, password))

    suspend fun checkJwt(token: String): ApiResponse<String>? {
        val response = service.checkjwt(token)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun logout(token: String): ApiResponse<LogoutRes>? {
        val response = service.logout(token)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun resetPasswordReq(email: String, role: String): ApiResponse<ResetPasswordRes>? {
        val response = service.resetPasswordReq(AuthUtil.basicAuth(), ResetPasswordReq(email, role))
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun resetPasswordSubmit(email: String, newPassword: String ,role: String): ApiResponse<ResetPasswordSubmitRes>? {
        val response = service.resetPasswordSubmit(AuthUtil.basicAuth(), ResetPasswordSubmit(email, newPassword, role))
        return if (response.isSuccessful) response.body() else null
    }

}
