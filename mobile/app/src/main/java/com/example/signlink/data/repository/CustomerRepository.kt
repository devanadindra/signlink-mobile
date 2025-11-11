package com.example.signlink.data.repository

import com.example.signlink.data.models.ApiResponse
import com.example.signlink.data.models.customer.PersonalRes
import com.example.signlink.data.services.CustomerService

class CustomerRepository(private val service: CustomerService) {
    suspend fun getPersonal(token: String): ApiResponse<PersonalRes>? {
        val response = service.getPersonal(token)
        return if (response.isSuccessful) response.body() else null
    }
}