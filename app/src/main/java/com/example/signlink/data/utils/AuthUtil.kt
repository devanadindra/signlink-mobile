package com.example.signlink.data.utils

import android.util.Base64

object AuthUtil {
    fun basicAuth(): String {
        val credentials = "admin:admin"
        val encoded = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        return "Basic $encoded"
    }
}
