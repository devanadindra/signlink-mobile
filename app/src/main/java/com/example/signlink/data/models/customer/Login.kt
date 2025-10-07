package com.example.signlink.data.models.customer

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val message: String
)