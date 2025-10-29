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
import com.example.signlink.data.utils.utils.parseErrorMessage


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _loginResult = MutableStateFlow<String?>(null)
    val loginResult: StateFlow<String?> = _loginResult

    private val _registerResult = MutableStateFlow<String?>(null)
    val registerResult: StateFlow<String?> = _registerResult

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun clearRegisterResult() {
        _registerResult.value = null
    }

    fun clearLoginResult() {
        _loginResult.value = null
    }

    fun login(context: Context, role: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(role, email, password)
                if (response.isSuccessful) {
                    val token = response.body()?.data?.token
                    val role = response.body()?.data?.role
                    if (!token.isNullOrEmpty()) {
                        AuthUtil.saveToken(context, token)
                        AuthUtil.saveRole(context, role.toString())
                        _loginResult.value = "success"
                    } else {
                        _loginResult.value = "Invalid token"
                    }
                } else {
                    val errorJson = response.errorBody()?.string()
                    _loginResult.value = parseErrorMessage(errorJson) ?: "Login failed"
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
                    val errorJson = response.errorBody()?.string()
                    _registerResult.value = parseErrorMessage(errorJson) ?: "Registration failed"
                }
            } catch (e: Exception) {
                _registerResult.value = "Error: ${e.localizedMessage ?: "unknown error"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun checkJwt(context: Context, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val token = AuthUtil.jwtAuth(context)
            if (token != null) {
                try {
                    val response = repository.checkJwt(token)
                    onResult(response?.data != null)
                } catch (_: Exception) {
                    onResult(false)
                }
            } else {
                onResult(false)
            }
        }
    }

    fun logout(context: Context, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val token = AuthUtil.jwtAuth(context)
            if (token != null) {
                try {
                    val response = repository.logout(token)
                    val isSuccess = response?.data?.loggedOut == true

                    if (isSuccess) {
                        AuthUtil.clearToken(context)
                    }

                    onResult(isSuccess)
                } catch (e: Exception) {
                    e.printStackTrace()
                    onResult(false)
                }
            } else {
                onResult(false)
            }
        }
    }


}
