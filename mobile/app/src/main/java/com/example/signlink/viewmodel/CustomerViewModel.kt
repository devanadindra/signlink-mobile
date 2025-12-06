package com.example.signlink.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.signlink.data.models.customer.PersonalRes
import com.example.signlink.data.repository.CustomerRepository
import com.example.signlink.data.utils.AuthUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val repository: CustomerRepository
) : ViewModel() {
    fun getPersonal(context: Context, onResult: (PersonalRes?) -> Unit) {
        viewModelScope.launch {
            val token = AuthUtil.jwtAuth(context)
            if (token != null) {
                try {
                    val response = repository.getPersonal(token)
                    val personal = response?.data
                    personal?.url?.let { url ->
                        if (url.isNotEmpty()) {
                            val fullUrl = if (url.startsWith("http://") || url.startsWith("https://")) {
                                url
                            } else {
                                "http://10.0.2.2:7777/api/$url"
                            }

                            AuthUtil.saveProfile(context, fullUrl)
                        }
                    }
                    onResult(personal)
                } catch (e: Exception) {
                    e.printStackTrace()
                    onResult(null)
                }
            } else {
                onResult(null)
            }
        }
    }
}