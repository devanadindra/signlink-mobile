@file:Suppress("DEPRECATION")

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
import androidx.activity.result.ActivityResult
import com.example.signlink.data.utils.utils.parseErrorMessage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _resetPasswordReqResult = MutableStateFlow<String?>(null)
    val resetPasswordReqResult: StateFlow<String?> = _resetPasswordReqResult

    private val _resetPasswordSubmitResult = MutableStateFlow<String?>(null)
    val resetPasswordSubmitResult: StateFlow<String?> = _resetPasswordSubmitResult

    private val _changePasswordResult = MutableStateFlow<String?>(null)
    val changePasswordSubmitResult: StateFlow<String?> = _changePasswordResult

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    private val _googleAuthMessage = MutableStateFlow<String?>(null)
    val googleAuthMessage: StateFlow<String?> = _googleAuthMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _registerComplete = MutableStateFlow(false)
    val registerComplete: StateFlow<Boolean> = _registerComplete

    private val _loginComplete = MutableStateFlow(false)
    val loginComplete: StateFlow<Boolean> = _loginComplete

    fun clearChangePasswordResult() {
        _changePasswordResult.value = null
        _isLoading.value = false
    }

    fun clearError() {
        _errorMessage.value = null
        _isLoading.value = false
    }

    fun clearSuccess() {
        _successMessage.value = null
        _isLoading.value = false
    }

    fun clearAll() {
        _loginComplete.value = false
        _registerComplete.value = false
        _successMessage.value = null
        _errorMessage.value = null
        _isLoading.value = false
    }


    fun login(context: Context, role: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.login(role, email, password)
                if (response.isSuccessful) {
                    val token = response.body()?.data?.token
                    val role = response.body()?.data?.role
                    if (!token.isNullOrEmpty()) {
                        AuthUtil.saveToken(context, token)
                        AuthUtil.saveRole(context, role.toString())
                        AuthUtil.saveLoginMethod(context, "manual")
                        _successMessage.value = "Selamat Datang 👋🏻"
                        _loginComplete.value = true
                    } else {
                        _errorMessage.value = "Token tidak valid"
                    }
                } else {
                    val errorJson = response.errorBody()?.string()
                    _errorMessage.value = parseErrorMessage(errorJson) ?: "Gagal Masuk"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.register(name, email, password)

                if (response.isSuccessful) {
                    _successMessage.value = "Pendaftaran berhasil"
                    _registerComplete.value = true
                } else {
                    val errorJson = response.errorBody()?.string()
                    _errorMessage.value = parseErrorMessage(errorJson)?.replace("\"", "") ?: "Pendaftaran Gagal"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Kesalahan: ${e.localizedMessage ?: "kesalahan yang tidak diketahui"}"
            }
        }
    }

    fun checkJwt(context: Context, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val token = AuthUtil.jwtAuth(context)

            if (token.isNullOrEmpty()) {
                AuthUtil.clearAll(context)
                onResult(false)
                return@launch
            }

            try {
                val response = repository.checkJwt(token)
                val isValid = response?.data != null

                if (!isValid) {
                    AuthUtil.clearAll(context)
                }

                onResult(isValid)

            } catch (_: Exception) {
                AuthUtil.clearAll(context)
                onResult(false)
            }
        }
    }


    fun logout(context: Context, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val token = AuthUtil.jwtAuth(context)
            val loginMethod = AuthUtil.getLoginMethod(context)
            if (token != null) {
                try {
                    val response = repository.logout(token)
                    val isSuccess = response?.data?.loggedOut == true

                    if (isSuccess) {
                        AuthUtil.clearAll(context)
                        clearAll()

                        if (loginMethod == "google") {
                            val googleClient = getGoogleSignInClient(context)
                            googleClient.signOut()
                        }
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

    fun resetPasswordReq(role: String, email: String) {
        viewModelScope.launch {
            try {
                val response = repository.resetPasswordReq(email, role)
                if (response != null) {
                    _resetPasswordReqResult.value = "Reset password link sent $email"
                } else {
                    _resetPasswordReqResult.value = "Gagal mengirim permintaan reset. Silakan coba lagi."
                }
            } catch (e: Exception) {
                _resetPasswordReqResult.value = "Kesalahan: ${e.localizedMessage ?: "kesalahan yang tidak diketahui"}"
            }
        }
    }

    fun resetPasswordSubmit(role: String, email: String, newPassword: String) {
        viewModelScope.launch {
            try {
                val response = repository.resetPasswordSubmit(email, newPassword, role)

                if (response != null && response.data?.message != null) {
                    _resetPasswordSubmitResult.value = response.data.message
                } else {
                    _resetPasswordSubmitResult.value = "Gagal mengatur ulang kata sandi. Silakan coba lagi."
                }

            } catch (e: Exception) {
                _resetPasswordSubmitResult.value = "Kesalahan: ${e.localizedMessage ?: "kesalahan yang tidak diketahui"}"
            }
        }
    }

    fun changePassword(context: Context, currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            val token = AuthUtil.jwtAuth(context)
            if (token != null) {
                try {
                    val response = repository.changePassword(token, currentPassword, newPassword)

                    if (response != null && response.data?.message != null) {
                        _changePasswordResult.value = response.data.message
                    } else {
                        if (response?.errors != null) {
                            _changePasswordResult.value = response.errors.first()
                        } else {
                            _changePasswordResult.value = "Gagal mengatur ulang kata sandi. Silakan coba lagi."
                        }
                    }

                } catch (e: Exception) {
                    _changePasswordResult.value = "Kesalahan: ${e.localizedMessage ?: "kesalahan yang tidak diketahui"}"
                }
            }
        }
    }

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("672778860454-6io63jm7npn2aj1oe1tasr2s99o1noe6.apps.googleusercontent.com")
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

    fun handleGoogleSignInResult(
        context: Context,
        result: ActivityResult,
        onComplete: (Boolean) -> Unit
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken

            if (idToken != null) {

                viewModelScope.launch {
                    try {
                        val response = repository.googleAuth(idToken)

                        if (response.isSuccessful) {
                            val token = response.body()?.data?.token
                            val role = response.body()?.data?.role

                            if (!token.isNullOrEmpty()) {
                                AuthUtil.saveToken(context, token)
                                AuthUtil.saveRole(context, role.toString())
                                AuthUtil.saveLoginMethod(context, "google")

                                _googleAuthMessage.value = "Selamat Datang 👋🏻"
                                onComplete(true)
                            } else {
                                _errorMessage.value = "Token tidak valid"
                            }
                        } else {
                            val errorJson = response.errorBody()?.string()
                            _errorMessage.value =
                                parseErrorMessage(errorJson) ?: "Gagal Masuk"
                        }
                    } catch (e: Exception) {
                        _errorMessage.value = e.message
                    }
                }

            } else {
                _errorMessage.value = "Token tidak ditemukan"
                onComplete(false)
            }

        } catch (e: Exception) {
            _errorMessage.value = e.localizedMessage
            onComplete(false)
        }
    }


}
