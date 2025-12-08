package com.example.signlink.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.signlink.data.models.latihan.LatihanByIdRes
import com.example.signlink.data.models.latihan.LatihanData
import com.example.signlink.data.models.latihan.LatihanReq
import com.example.signlink.data.utils.utils.parseErrorMessage
import com.example.signlink.data.repository.LatihanRepository
import com.example.signlink.data.utils.AuthUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LatihanViewModel @Inject constructor(
    private val repository: LatihanRepository
) : ViewModel() {

    private val _latihanList = MutableStateFlow<List<LatihanData>>(emptyList())
    val latihanList: StateFlow<List<LatihanData>> = _latihanList

    private val _latihanDetail = MutableStateFlow<LatihanByIdRes?>(null)
    val latihanDetail = _latihanDetail.asStateFlow()


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

    fun getLatihanById(context: Context, latihanId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val token = AuthUtil.jwtAuth(context)
            if (token != null) {
                try {
                    val data = repository.getLatihanById(token, latihanId)
                    _latihanDetail.value = data
                } catch (_: Exception) {
                    _latihanDetail.value = null
                }
            } else {
                _latihanDetail.value = null
            }
            _isLoading.value = false
        }
    }

    fun addLatihan(context: Context, name: String, soalLatihan: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true
            val token = AuthUtil.jwtAuth(context)

            if (token.isNullOrEmpty()) {
                _errorMessage.value = "Token not found"
                _isLoading.value = false
                return@launch
            }

            try {
                val req = LatihanReq(name, soalLatihan)
                val response = repository.addLatihan(token, req)

                if (response.isSuccessful) {
                    val body = response.body()
                    _successMessage.value = body?.data?.message ?: "Upload successful"
                } else {
                    val errorJson = response.errorBody()?.string()
                    _errorMessage.value = parseErrorMessage(errorJson) ?: "Upload failed"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun deleteLatihan(context: Context, latihanId: String, page: Int, limit: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val token = AuthUtil.jwtAuth(context)

            if (token.isNullOrEmpty()) {
                _errorMessage.value = "Token not found"
                _isLoading.value = false
                return@launch
            }

            try {
                val response = repository.deleteLatihan(token, latihanId)

                if (response.isSuccessful) {
                    val body = response.body()
                    _successMessage.value = body?.data?.message ?: "Delete successful"
                    getAllLatihan(context, page, limit)
                } else {
                    val errorJson = response.errorBody()?.string()
                    _errorMessage.value = parseErrorMessage(errorJson) ?: "Delete failed"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getAllLatihan(context: Context, page: Int, limit: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val token = AuthUtil.jwtAuth(context)
            try {
                val result = repository.getAllLatihan(token.toString(), page, limit)

                if (result != null) {
                    _latihanList.value = result
                    _successMessage.value = "Data berhasil dimuat"
                } else {
                    _errorMessage.value = "Data gagal dimuat"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

}

