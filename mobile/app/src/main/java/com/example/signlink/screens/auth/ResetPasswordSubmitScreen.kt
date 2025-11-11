package com.example.signlink.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.signlink.R
import com.example.signlink.ui.theme.SignLinkTeal
import com.example.signlink.ui.theme.DarkText
import com.example.signlink.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun ResetPasswordSubmitScreen(
    viewModel: AuthViewModel,
    email: String,
    role: String,
    onPasswordResetSuccess: () -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var newPasswordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val submitResult by viewModel.resetPasswordSubmitResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    fun validateForm(): Boolean {
        var isValid = true
        newPasswordError = null
        confirmPasswordError = null
        successMessage = null

        if (newPassword.isBlank() || newPassword.length < 6) {
            newPasswordError = "Kata sandi baru minimal 6 karakter."
            isValid = false
        }

        if (confirmPassword.isBlank()) {
            confirmPasswordError = "Konfirmasi kata sandi tidak boleh kosong."
            isValid = false
        } else if (newPassword != confirmPassword) {
            confirmPasswordError = "Konfirmasi kata sandi tidak cocok."
            isValid = false
        }

        return isValid
    }

    LaunchedEffect(submitResult) {
        submitResult?.let { result ->
            if (result.contains("success", ignoreCase = true)) {
                successMessage = "Kata Sandi Berhasil Diperbarui! Silahkan masuk."
                delay(2000L)
                onPasswordResetSuccess()
            } else if (!result.contains("null", ignoreCase = true)) {
                successMessage = null
            }
        }
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
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Atur Kata Sandi Baru",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Email: $email \nMode: $role",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(40.dp))

        Text(text = "Kata Sandi Baru", modifier = Modifier.fillMaxWidth(), color = DarkText, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it; newPasswordError = null },
            placeholder = { Text("Masukkan kata sandi baru") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Toggle password visibility")
                }
            },
            isError = newPasswordError != null,
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
        if (newPasswordError != null) {
            Text(
                text = newPasswordError!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp),
                textAlign = TextAlign.Start
            )
        }
        Spacer(modifier = Modifier.height(14.dp))

        Text(text = "Konfirmasi Kata Sandi", modifier = Modifier.fillMaxWidth(), color = DarkText, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it; confirmPasswordError = null },
            placeholder = { Text("Konfirmasi kata sandi baru") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = confirmPasswordError != null,
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
        if (confirmPasswordError != null) {
            Text(
                text = confirmPasswordError!!,
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
                    .padding(top = 16.dp),
                textAlign = TextAlign.Center
            )
        } else {
            submitResult?.let {
                if (!it.contains("null", ignoreCase = true) && !it.contains("success", ignoreCase = true)) {
                    Text(
                        text = it.replace("\"", ""),
                        color = Color.Red,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
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
                    viewModel.resetPasswordSubmit(role, email, newPassword)
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
                Text("Ubah Kata Sandi", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Kembali ke Halaman Masuk",
                color = SignLinkTeal,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onPasswordResetSuccess)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}