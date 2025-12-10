package com.example.signlink.data.models.customer

import com.google.gson.annotations.SerializedName
import java.io.File

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

data class UpdateProfileReq(
    val name: String,
    val email: String
)

data class ChangePaswordReq(
    @SerializedName("current_password")
    val currentPassword: String,

    @SerializedName("new_password")
    val newPassword: String
)

data class AvatarReq(
    val avatar: File
)