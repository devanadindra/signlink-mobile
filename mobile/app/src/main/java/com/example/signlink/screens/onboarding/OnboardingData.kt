package com.example.signlink.screens.onboarding

import androidx.annotation.DrawableRes
import com.example.signlink.R

data class OnboardingItem(
    @DrawableRes val illustrationId: Int,
    val title: String,
    val description: String,
    val isLastPage: Boolean = false
)

val onboardingPages = listOf(
    OnboardingItem(
        illustrationId = R.drawable.illustration,
        title = "Selamat Datang di SignLink!",
        description = "Temukan cara baru untuk berkomunikasi tanpa batas. SignLink membantu Anda memahami dan menerjemahkan bahasa isyarat secara instan dan akurat."
    ),
    OnboardingItem(
        illustrationId = R.drawable.illustration,
        title = "Komunikasi Instan dan Akurat",
        description = "Teknologi AI kami menerjemahkan bahasa isyarat Anda secara real-time ke dalam teks dan ucapan, membuat dialog menjadi lancar."
    ),
    OnboardingItem(
        illustrationId = R.drawable.illustration,
        title = "Belajar dan Terhubung",
        description = "Mulai perjalanan Anda dalam memahami dan menggunakan bahasa isyarat. SignLink adalah jembatan komunikasi Anda menuju dunia yang inklusif.",
        isLastPage = true
    )
)