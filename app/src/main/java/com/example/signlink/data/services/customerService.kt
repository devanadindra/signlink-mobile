package com.example.signlink.data.services

import com.example.signlink.data.models.customer.RegisterRequest
import com.example.signlink.data.models.customer.RegisterResponse
import com.example.signlink.data.models.customer.LoginRequest
import com.example.signlink.data.models.customer.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface CustomerService {
    @POST("user/login")
    suspend fun login(
        @Header("Authorization") authHeader: String,
        @Body req: LoginRequest
    ): Response<LoginResponse>

    @POST("user/register")
    suspend fun register(
        @Header("Authorization") authHeader: String,
        @Body req: RegisterRequest
    ): Response<RegisterResponse>
}
