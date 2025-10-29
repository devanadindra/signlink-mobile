package com.example.signlink.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.signlink.data.models.kamus.KamusData
import com.example.signlink.data.models.kamus.KamusReq
import com.example.signlink.data.utils.utils.parseErrorMessage
import com.example.signlink.data.repository.KamusRepository
import com.example.signlink.data.utils.AuthUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class KamusViewModel @Inject constructor(
    private val repository: KamusRepository
) : ViewModel() {

    private val _kamusList = MutableStateFlow<List<KamusData>>(emptyList())
    val kamusList: StateFlow<List<KamusData>> = _kamusList

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearSuccess() {
        _successMessage.value = null
    }

    fun getKamus(context: Context, kategori: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val token = AuthUtil.jwtAuth(context)
            if (token != null) {
                try {
                    val data = repository.getKamus(token, kategori)
                    _kamusList.value = data ?: emptyList()
                } catch (_: Exception) {
                    _kamusList.value = emptyList()
                }
            } else {
                _kamusList.value = emptyList()
            }
            _isLoading.value = false
        }
    }

    fun addKamus(context: Context, arti: String, kategori: String, videoFile: File) {
        viewModelScope.launch {
            _isLoading.value = true
            val token = AuthUtil.jwtAuth(context)

            if (token.isNullOrEmpty()) {
                _errorMessage.value = "Token not found"
                _isLoading.value = false
                return@launch
            }

            try {
                val req = KamusReq(arti, kategori, videoFile)
                val response = repository.addKamus(token, req)

                if (response.isSuccessful) {
                    val body = response.body()
                    _successMessage.value = body?.data?.message ?: "Upload successful"
                    getKamus(context, kategori)
                } else {
                    val errorJson = response.errorBody()?.string()
                    _errorMessage.value = parseErrorMessage(errorJson) ?: "Upload failed"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

}

