package com.example.signlink.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.signlink.data.models.customer.PersonalRes
import com.example.signlink.data.repository.CustomerRepository
import com.example.signlink.data.utils.AuthUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val repository: CustomerRepository
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun clearError() {
        _errorMessage.value = null
        _isLoading.value = false
    }

    fun clearSuccess() {
        _successMessage.value = null
        _isLoading.value = false
    }

    fun clearUpdateProfileResult() {
        _errorMessage.value = null
        _successMessage.value = null
        _isLoading.value = false
    }

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

    fun updateProfile(
        context: Context,
        name: String,
        email: String
    ) {
        viewModelScope.launch {
            val token = AuthUtil.jwtAuth(context)
            _isLoading.value = true
            if (token != null) {
                try {
                    val role = AuthUtil.getRole(context).toString()
                    val response = repository.updateProfile(token, name, email, role)
                    val personal = response?.data

                    if (personal != null) {
                        _successMessage.value = "Perubahan profil berhasil disimpan!"
                    } else {
                        _errorMessage.value = "Gagal menyimpan perubahan. Silakan coba lagi."
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _errorMessage.value = e.message
                }
            } else {
                _errorMessage.value = "Gagal menyimpan perubahan. Silakan coba lagi."
            }
        }
    }
}