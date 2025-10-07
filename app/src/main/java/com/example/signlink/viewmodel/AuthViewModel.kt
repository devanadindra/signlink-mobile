package com.example.signlink.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.signlink.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val response = repository.login(email, password)
            if (response.isSuccessful) {
                // TODO: handle success
            } else {
                // TODO: handle error
            }
        }
    }

    private val _registerResult = MutableStateFlow<String?>(null)
    val registerResult: StateFlow<String?> = _registerResult

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

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
}
