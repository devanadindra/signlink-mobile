package com.example.signlink.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.signlink.data.di.ApiConfig
import com.example.signlink.data.models.customer.PersonalRes
import com.example.signlink.data.repository.CustomerRepository
import com.example.signlink.data.utils.AuthUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

private const val BASE_URL = ApiConfig.BASE_URL

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
                                val cleanUrl = url.removePrefix("/")
                                BASE_URL + cleanUrl
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
                    val response = repository.updateProfile(token, name, email)
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

    fun addAvatar(
        context: Context,
        avatar: File
    ) {
        viewModelScope.launch {
            val token = AuthUtil.jwtAuth(context)
            _isLoading.value = true
            if (token != null) {
                try {
                    val response = repository.addAvatar(token, avatar)
                    val res = response?.data

                    if (res != null) {
                        res.avatarUrl.let { url ->
                            if (url.isNotEmpty()) {
                                val fullUrl = if (url.startsWith("http://") || url.startsWith("https://")) {
                                    url
                                } else {
                                    val cleanUrl = url.removePrefix("/")
                                    "http://10.0.2.2:7777/api/$cleanUrl"
                                }

                                AuthUtil.saveProfile(context, fullUrl)
                            }
                        }
                        _successMessage.value = "avatar berhasil disimpan!"
                    } else {
                        _errorMessage.value = "Gagal menyimpan avatar. Silakan coba lagi."
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _errorMessage.value = e.message
                }
            } else {
                _errorMessage.value = "Gagal menyimpan avatar. Silakan coba lagi."
            }
        }
    }

    fun deleteAvatar(
        context: Context
    ) {
        viewModelScope.launch {
            val token = AuthUtil.jwtAuth(context)
            _isLoading.value = true
            if (token != null) {
                try {
                    val response = repository.deleteAvatar(token)
                    val res = response?.data

                    if (res != null) {
                        _successMessage.value = res.message
                        AuthUtil.clearProfile(context)
                    } else {
                        _errorMessage.value = "Gagal menghapus foto. Silakan coba lagi."
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _errorMessage.value = e.message
                }
            } else {
                _errorMessage.value = "Gagal menghapus foto. Silakan coba lagi."
            }
        }
    }
}