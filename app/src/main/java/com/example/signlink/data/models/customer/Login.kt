package com.example.signlink.data.models.customer

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginData(
    val token: String,
    val expires: String,
)

data class LoginResponse(
    val data: LoginData?,
    val errors: Any?
)