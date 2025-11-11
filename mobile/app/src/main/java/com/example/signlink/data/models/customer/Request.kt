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

data class ResetPasswordReq(
    val email: String,
    val role: String,
)
data class ResetPasswordSubmit(
    val email: String,
    val newPassword: String,
    val role: String,
)