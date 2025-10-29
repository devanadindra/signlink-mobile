package com.example.signlink.data.models.customer

data class RegisterReq(
    val name: String,
    val email: String,
    val password: String
)

data class LoginReq(
    val role: String,
    val email: String,
    val password: String,
)