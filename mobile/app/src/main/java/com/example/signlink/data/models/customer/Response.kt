package com.example.signlink.data.models.customer

import com.google.gson.annotations.SerializedName

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
    val url: String,
    @SerializedName("google_id")
    val googleId : String,
    @SerializedName("has_password")
    val hasPassword: Boolean
)

data class ResetPasswordRes(
    val email: String,
)
data class ResetPasswordSubmitRes(
    val message: String,
)

data class ChangePasswordRes(
    val message: String,
)

data class AvatarRes(
    val avatarUrl: String,
    val message: String
)

data class DeleteAvatarRes(
    val message: String
)