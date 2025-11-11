package com.example.signlink.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.signlink.R
import com.example.signlink.ui.theme.SignLinkTeal
import com.example.signlink.ui.theme.DarkText
import com.example.signlink.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel,
    onBackToLogin: () -> Unit,
    onResetEmailSent: (email: String, role: String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    var role by remember { mutableStateOf("CUSTOMER") }
    var tapCount by remember { mutableIntStateOf(0) }
    var lastTapTime by remember { mutableLongStateOf(0L) }

    var successMessage by remember { mutableStateOf<String?>(null) }
    var roleChangeMessage by remember { mutableStateOf<String?>(null) }

    val resetPasswordResult by viewModel.resetPasswordReqResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(resetPasswordResult) {
        resetPasswordResult?.let { result ->
            if (result.contains("Reset password link sent", ignoreCase = true)) {
                successMessage = "Tautan reset telah dikirim ke $email!"
                scope.launch {
                    delay(1500L)
                    // Navigasi ke layar submit, membawa email DAN role
                    onResetEmailSent(email, role)
                }
            } else if (!result.contains("null", ignoreCase = true)) {
                successMessage = null
            }
        }
    }

    LaunchedEffect(tapCount) {
        if (tapCount > 0) {
            delay(800L)
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastTapTime >= 800L) {
                tapCount = 0
            }
        }
    }

    fun validateForm(): Boolean {
        var isValid = true
        emailError = null
        successMessage = null

        if (email.isBlank()) {
            emailError = "Email tidak boleh kosong."
            isValid = false
        } else if (!isValidEmail(email)) {
            emailError = "Format email tidak valid."
            isValid = false
        }
        return isValid
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(74.dp))

        Image(
            painter = painterResource(id = R.drawable.signlink_logo),
            contentDescription = "SignLink Logo",
            modifier = Modifier
                .size(80.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            val currentTime = System.currentTimeMillis()
                            if (currentTime - lastTapTime < 800L) {
                                tapCount++
                                if (tapCount == 6) {
                                    role = if (role == "CUSTOMER") "ADMIN" else "CUSTOMER"
                                    roleChangeMessage = "Mode: $role diaktifkan! (Tersembunyi)"
                                    tapCount = 0
                                }
                            } else {
                                tapCount = 1
                            }
                            lastTapTime = currentTime
                        }
                    )
                }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Lupa Kata Sandi",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            textAlign = TextAlign.Center
        )

        if (roleChangeMessage != null) {
            Text(
                text = roleChangeMessage!!,
                color = if (role == "ADMIN") SignLinkTeal else Color.Gray,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Masukkan email yang terdaftar untuk menerima tautan reset kata sandi.",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))

        Text(text = "Email", modifier = Modifier.fillMaxWidth(), color = DarkText, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; emailError = null },
            placeholder = { Text("Masukkan email terdaftar") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = emailError != null,
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                focusedBorderColor = SignLinkTeal,
                unfocusedBorderColor = Color.LightGray,
                errorBorderColor = Color.Red,
            )
        )
        if (emailError != null) {
            Text(
                text = emailError!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp),
                textAlign = TextAlign.Start
            )
        }

        if (successMessage != null) {
            Text(
                text = successMessage!!,
                color = Color(0xFF4CAF50),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                textAlign = TextAlign.Center
            )
        } else {
            resetPasswordResult?.let {
                if (!it.contains("null", ignoreCase = true) && !it.contains("Reset password link sent", ignoreCase = true)) {
                    Text(
                        text = it.replace("\"", ""),
                        color = Color.Red,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        val showLoading = isLoading || successMessage != null

        Button(
            onClick = {
                if (!showLoading && validateForm()) {
                    // Panggil fungsi ViewModel dengan email dan role yang terdeteksi
                    viewModel.resetPasswordReq(email = email, role = role)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SignLinkTeal),
            shape = RoundedCornerShape(50),
            enabled = !showLoading
        ) {
            if (showLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Kirim Tautan Reset", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Batalkan dan Kembali ke Masuk",
            color = Color.Gray,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable(onClick = onBackToLogin)
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}