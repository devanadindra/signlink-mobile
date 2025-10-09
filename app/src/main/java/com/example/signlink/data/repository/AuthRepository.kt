package com.example.signlink.data.repository

import android.content.Context
import com.example.signlink.data.models.customer.CheckJWTResponse
import com.example.signlink.data.services.CustomerService
import com.example.signlink.data.utils.AuthUtil
import com.example.signlink.data.models.customer.LoginRequest
import com.example.signlink.data.models.customer.RegisterRequest

class AuthRepository(private val service: CustomerService) {
    suspend fun login(email: String, password: String) =
        service.login(AuthUtil.basicAuth(), LoginRequest(email, password))

    suspend fun register(name: String, email: String, password: String) =
        service.register(AuthUtil.basicAuth(), RegisterRequest(name, email, password))

    suspend fun checkJwt(token: String): CheckJWTResponse? {
        val response = service.checkjwt(token)
        return if (response.isSuccessful) response.body() else null
    }

}
