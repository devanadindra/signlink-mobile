package com.example.signlink.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.signlink.data.models.kamus.KamusData
import com.example.signlink.data.repository.KamusRepository
import com.example.signlink.data.utils.AuthUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KamusViewModel @Inject constructor(
    private val repository: KamusRepository
) : ViewModel() {

    private val _kamusList = MutableStateFlow<List<KamusData>>(emptyList())
    val kamusList: StateFlow<List<KamusData>> = _kamusList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun getKamus(context: Context, kategori: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val token = AuthUtil.jwtAuth(context)
            if (token != null) {
                try {
                    val data = repository.getKamus(token, kategori)
                    _kamusList.value = data ?: emptyList()
                } catch (e: Exception) {
                    _kamusList.value = emptyList()
                }
            } else {
                _kamusList.value = emptyList()
            }
            _isLoading.value = false
        }
    }
}

