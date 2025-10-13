package com.example.signlink.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay

private fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

@Composable
fun SignUpScreen(
    viewModel: AuthViewModel,
    onSignUpSuccess: () -> Unit,
    onLoginFailed: () -> Unit,
    onLoginClicked: () -> Unit
) {
    // State Input
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // State Error Per Field
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val registerResult by viewModel.registerResult.collectAsState()
    val loginResult by viewModel.loginResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // State untuk mengontrol pesan sukses/status
    var statusMessage by remember { mutableStateOf<String?>(null) }
    // State internal untuk menunjukkan bahwa login otomatis sedang berjalan,
    // agar spinner tetap aktif setelah register sukses
    var isAutoLoggingIn by remember { mutableStateOf(false) }

    LaunchedEffect(password, confirmPassword) {
        if (password != confirmPassword) {
            confirmPasswordError = null
        }
    }

    fun validateForm(): Boolean {
        var isValid = true

        // ... (Logika Validasi yang sudah ada)

        if (name.isBlank()) {
            nameError = "Nama lengkap tidak boleh kosong."
            isValid = false
        } else {
            nameError = null
        }

        if (email.isBlank()) {
            emailError = "Email tidak boleh kosong."
            isValid = false
        } else if (!isValidEmail(email)) {
            emailError = "Format email tidak valid."
            isValid = false
        } else {
            emailError = null
        }

        if (password.isBlank()) {
            passwordError = "Password tidak boleh kosong."
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Password minimal 6 karakter."
            isValid = false
        } else {
            passwordError = null
        }

        if (confirmPassword.isBlank()) {
            confirmPasswordError = "Konfirmasi password tidak boleh kosong."
            isValid = false
        } else if (password != confirmPassword) {
            confirmPasswordError = "Password tidak cocok."
            isValid = false
        } else {
            confirmPasswordError = null
        }

        return isValid
    }

    // --- Logic Status Pesan Error Umum ---
    val generalErrorMessage = if (passwordError == null && confirmPasswordError == null && statusMessage == null) {
        if (registerResult?.contains("success", true) == false || loginResult?.contains("success", true) == false) {
            // Tampilkan pesan error jika register/login gagal, dan tidak ada pesan sukses yang sedang ditampilkan
            registerResult ?: loginResult
        } else {
            null
        }
    } else {
        null
    }

    val context = LocalContext.current

    // Logic untuk Register dan Login Otomatis
    LaunchedEffect(registerResult) {
        registerResult?.let {
            if (it.contains("success", true)) {
                // Register Sukses: Tampilkan pesan, set status login, dan mulai login
                statusMessage = "Pendaftaran berhasil! Mencoba masuk..."
                delay(500L) // Delay singkat untuk melihat pesan register
                isAutoLoggingIn = true // Aktifkan spinner
                viewModel.login(context, email, password)
            }
        }
    }

    LaunchedEffect(loginResult) {
        loginResult?.let {
            isAutoLoggingIn = false // Matikan status login otomatis
            if (it.contains("success", true)) {
                // Login Sukses: Tampilkan pesan final dan navigasi
                statusMessage = "Berhasil masuk! Selamat datang."
                delay(1500L) // Delay untuk melihat pesan sukses final
                onSignUpSuccess()
            } else if (it.isNotBlank() && !it.contains("success", true)) {
                // Login Gagal setelah Register Sukses
                statusMessage = null
                onLoginFailed()
            }
        }
    }

    // Kombinasi state loading
    val showLoading = isLoading || isAutoLoggingIn

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        Image(
            painter = painterResource(id = R.drawable.signlink_logo),
            contentDescription = "SignLink Logo",
            modifier = Modifier.size(80.dp)
        )
        Text(
            text = "Selamat Datang!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(40.dp))

        // --- Nama Lengkap ---
        Text(
            text = "Nama Lengkap",
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Medium,
            color = DarkText
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it; nameError = null },
            placeholder = { Text("Masukkan nama lengkap") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = nameError != null,
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = SignLinkTeal,
                unfocusedBorderColor = Color.LightGray,
                errorBorderColor = Color.Red,
            )
        )
        if (nameError != null) {
            Text(text = nameError!!, color = Color.Red, style = MaterialTheme.typography.bodySmall,                 modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
                textAlign = TextAlign.Start)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Email",
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Medium,
            color = DarkText
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; emailError = null },
            placeholder = { Text("Masukkan email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = emailError != null,
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = SignLinkTeal,
                unfocusedBorderColor = Color.LightGray,
                errorBorderColor = Color.Red,
            )
        )
        if (emailError != null) {
            Text(text = emailError!!, color = Color.Red, style = MaterialTheme.typography.bodySmall,                 modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
                textAlign = TextAlign.Start)
        }

        Spacer(modifier = Modifier.height(4.dp))

        // --- Password ---
        Text(
            text = "Password",
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Medium,
            color = DarkText
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; passwordError = null },
            placeholder = { Text("Masukkan password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Toggle password visibility")
                }
            },
            isError = passwordError != null || confirmPasswordError != null,
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = SignLinkTeal,
                unfocusedBorderColor = Color.LightGray,
                errorBorderColor = Color.Red,
            )
        )
        if (passwordError != null) {
            Text(text = passwordError!!, color = Color.Red, style = MaterialTheme.typography.bodySmall,                 modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
                textAlign = TextAlign.Start)
        }

        Spacer(modifier = Modifier.height(4.dp))

        // --- Konfirmasi Password ---
        Text(
            text = "Konfirmasi Password",
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Medium,
            color = DarkText
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it; confirmPasswordError = null },
            placeholder = { Text("Masukkan ulang password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image =
                    if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(imageVector = image, contentDescription = "Toggle password visibility")
                }
            },
            isError = confirmPasswordError != null,
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = SignLinkTeal,
                unfocusedBorderColor = Color.LightGray,
                errorBorderColor = Color.Red,
            )
        )
        if (confirmPasswordError != null) {
            Text(text = confirmPasswordError!!, color = Color.Red, style = MaterialTheme.typography.bodySmall,                 modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
                textAlign = TextAlign.Start)
        }

        // --- PESAN KESELURUHAN (Sukses/Status/Error) ---
        if (statusMessage != null) {
            Text(
                text = statusMessage!!,
                color = if (statusMessage!!.contains("sukses") || statusMessage!!.contains("berhasil")) Color(0xFF4CAF50) else Color.DarkGray,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                textAlign = TextAlign.Center
            )
        } else {
            generalErrorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red, // Warna merah untuk error
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 2.dp),
                    textAlign = TextAlign.Start
                )
            }
        }


        Spacer(modifier = Modifier.height(20.dp))

        // --- TOMBOL DAFTAR ---
        Button(
            onClick = {
                if (!showLoading && validateForm()) { // Mencegah klik saat loading
                    viewModel.register(name, email, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SignLinkTeal),
            shape = RoundedCornerShape(50),
            enabled = !showLoading // Nonaktifkan tombol saat loading/auto-login
        ) {
            if (showLoading) {
                // Tampilkan CircularProgressIndicator saat loading
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Daftar", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 1.dp,
                color = Color.LightGray
            )
            Text(" Atau ", modifier = Modifier.padding(horizontal = 8.dp), color = Color.Gray)
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 1.dp,
                color = Color.LightGray
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedButton(
            onClick = { /* Handle Google Login */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(50)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkText, containerColor = Color.White),
            shape = RoundedCornerShape(50)
        ) {
            Image(
                painter = painterResource(id = R.drawable.google),
                contentDescription = "Google Logo",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("Lanjutkan dengan Google", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Text("Sudah memiliki akun? ", color = Color.Gray, fontSize = 16.sp)
            Text(
                text = "Masuk",
                color = SignLinkTeal,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onLoginClicked)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}