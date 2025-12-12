package com.example.signlink.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.signlink.data.models.kuis.KuisByIdRes
import com.example.signlink.data.models.kuis.KuisData
import com.example.signlink.data.models.kuis.KuisReq
import com.example.signlink.data.models.kuis.StatsKuisByUserIdRes
import com.example.signlink.data.models.kuis.StatsKuisReq
import com.example.signlink.data.utils.utils.parseErrorMessage
import com.example.signlink.data.repository.KuisRepository
import com.example.signlink.data.utils.AuthUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KuisViewModel @Inject constructor(
    private val repository: KuisRepository
) : ViewModel() {

    private val _kuisList = MutableStateFlow<List<KuisData>>(emptyList())
    val kuisList: StateFlow<List<KuisData>> = _kuisList

    private val _statsKuisByuserIdList = MutableStateFlow<List<StatsKuisByUserIdRes>>(emptyList())
    val statsKuisByuserIdList: StateFlow<List<StatsKuisByUserIdRes>> = _statsKuisByuserIdList

    private val _kuisDetail = MutableStateFlow<KuisByIdRes?>(null)
    val kuisDetail = _kuisDetail.asStateFlow()


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

    fun getKuisById(context: Context, kuisId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val token = AuthUtil.jwtAuth(context)
            if (token != null) {
                try {
                    val data = repository.getKuisById(token, kuisId)
                    _kuisDetail.value = data
                } catch (_: Exception) {
                    _kuisDetail.value = null
                }
            } else {
                _kuisDetail.value = null
            }
            _isLoading.value = false
        }
    }

    fun addKuis(context: Context, req: KuisReq) {
        viewModelScope.launch {
            _isLoading.value = true
            val token = AuthUtil.jwtAuth(context)

            if (token.isNullOrEmpty()) {
                _errorMessage.value = "Token not found"
                _isLoading.value = false
                return@launch
            }

            try {
                val response = repository.addKuis(token, req)

                if (response.isSuccessful) {
                    val body = response.body()
                    _successMessage.value = body?.data?.message ?: "Add Kuis successful"
                } else {
                    val errorJson = response.errorBody()?.string()
                    _errorMessage.value = parseErrorMessage(errorJson) ?: "Add Kuis failed"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun addStatsKuis(context: Context, kuisId: String, score: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val token = AuthUtil.jwtAuth(context)

            if (token.isNullOrEmpty()) {
                _errorMessage.value = "Token not found"
                _isLoading.value = false
                return@launch
            }

            try {
                val req = StatsKuisReq(kuisId, score)
                val response = repository.addStatsKuis(token, req)

                if (response.isSuccessful) {
                    val body = response.body()
                    _successMessage.value = body?.data?.message ?: "Save stats successful"
                } else {
                    val errorJson = response.errorBody()?.string()
                    _errorMessage.value = parseErrorMessage(errorJson) ?: "Save stats failed"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun deleteKuis(context: Context, kuisId: String, page: Int, limit: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val token = AuthUtil.jwtAuth(context)

            if (token.isNullOrEmpty()) {
                _errorMessage.value = "Token not found"
                _isLoading.value = false
                return@launch
            }

            try {
                val response = repository.deleteKuis(token, kuisId)

                if (response.isSuccessful) {
                    val body = response.body()
                    _successMessage.value = body?.data?.message ?: "Delete successful"
                    getAllKuis(context, page, limit)
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

    fun getAllKuis(context: Context, page: Int, limit: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val token = AuthUtil.jwtAuth(context)
            try {
                val result = repository.getAllKuis(token.toString(), page, limit)

                if (result != null) {
                    _kuisList.value = result
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

    fun getStatsKuisByUserId(context: Context) {
        viewModelScope.launch {
            Log.d("DEBUG", "Mulai getStatsKuisByUserId()")
            _isLoading.value = true

            val token = AuthUtil.jwtAuth(context)
            Log.d("DEBUG", "Token: $token")

            try {
                val result = repository.getStatsKuisByUserId(token.toString())
                Log.d("DEBUG", "Result dari repository: $result")

                if (result != null) {
                    _statsKuisByuserIdList.value = result
                    _successMessage.value = "Data berhasil dimuat"
                    Log.d("DEBUG", "Berhasil: $result")
                } else {
                    _errorMessage.value = "Data gagal dimuat"
                    Log.e("DEBUG", "Result NULL")
                }

            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
                Log.e("DEBUG", "Catch error: ", e)
            } finally {
                _isLoading.value = false
                Log.d("DEBUG", "Selesai getStatsKuisByUserId()")
            }
        }
    }

}

