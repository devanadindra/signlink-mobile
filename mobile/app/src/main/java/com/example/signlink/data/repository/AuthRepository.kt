package com.example.signlink.data.repository

import com.example.signlink.data.models.ApiResponse
import com.example.signlink.data.models.customer.LoginReq
import com.example.signlink.data.models.customer.LogoutRes
import com.example.signlink.data.models.customer.RegisterReq
import com.example.signlink.data.services.CustomerService
import com.example.signlink.data.utils.AuthUtil

class AuthRepository(private val service: CustomerService) {
    suspend fun login(email: String, password: String) =
        service.login(AuthUtil.basicAuth(), LoginReq(email, password))

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

}
