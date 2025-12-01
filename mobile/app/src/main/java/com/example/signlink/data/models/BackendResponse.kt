package com.example.signlink.data.models

data class ApiResponse<T>(
    val data: T?,
    val errors: List<String>?
)