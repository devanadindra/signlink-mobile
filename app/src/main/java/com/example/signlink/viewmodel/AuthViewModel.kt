package com.example.signlink.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.signlink.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.signlink.data.utils.AuthUtil
import android.content.Context
import android.util.Log


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _loginResult = MutableStateFlow<String?>(null)
    val loginResult: StateFlow<String?> = _loginResult

    private val _registerResult = MutableStateFlow<String?>(null)
    val registerResult: StateFlow<String?> = _registerResult

    private val _jwtResult = MutableStateFlow<String?>(null)
    val jwtResult: StateFlow<String?> = _jwtResult

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun login(context: Context, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                if (response.isSuccessful) {
                    val token = response.body()?.data?.token
                    if (!token.isNullOrEmpty()) {
                        AuthUtil.saveToken(context, token)
                        _loginResult.value = "success"
                    } else {
                        _loginResult.value = "Invalid token"
                    }
                } else {
                    _loginResult.value = "Login failed"
                }
            } catch (e: Exception) {
                _loginResult.value = e.message
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _registerResult.value = null

                val response = repository.register(name, email, password)

                if (response.isSuccessful) {
                    _registerResult.value = "Registration success"
                } else {
                    _registerResult.value = "Registration failed: ${response.message()}"
                }
            } catch (e: Exception) {
                _registerResult.value = "Error: ${e.localizedMessage ?: "unknown error"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Di AuthViewModel
    fun checkJwt(context: Context, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val token = AuthUtil.jwtAuth(context)
            if (token != null) {
                try {
                    val response = repository.checkJwt(token)
                    onResult(response?.data != null)
                } catch (e: Exception) {
                    onResult(false)
                }
            } else {
                onResult(false)
            }
        }
    }



}
