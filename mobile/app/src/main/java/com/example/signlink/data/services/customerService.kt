package com.example.signlink.data.services

import com.example.signlink.data.models.ApiResponse
import com.example.signlink.data.models.customer.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface CustomerService {
    @POST("user/login")
    suspend fun login(
        @Header("Authorization") authHeader: String,
        @Body req: LoginRequest
    ): Response<ApiResponse<LoginData>>

    @POST("user/register")
    suspend fun register(
        @Header("Authorization") authHeader: String,
        @Body req: RegisterRequest
    ): Response<ApiResponse<RegisterResponse>>

    @GET("user/check-jwt")
    suspend fun checkjwt(
        @Header("Authorization") authHeader: String,
    ): Response<ApiResponse<String>>

    @POST("user/logout")
    suspend fun logout(
        @Header("Authorization") authHeader: String,
    ): Response<ApiResponse<LogoutResponse>>
}
