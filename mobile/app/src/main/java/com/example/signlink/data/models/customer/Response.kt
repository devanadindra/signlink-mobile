package com.example.signlink.data.models.customer

data class RegisterRes(
    val message: String
)

data class LogoutRes(
    val loggedOut: Boolean
)

data class LoginRes(
    val role: String,
    val token: String,
    val expires: String,
)

data class PersonalRes(
    val id: String,
    val name: String,
    val email: String,
    val url: String
)

data class ResetPasswordRes(
    val email: String,
)
data class ResetPasswordSubmitRes(
    val message: String,
)