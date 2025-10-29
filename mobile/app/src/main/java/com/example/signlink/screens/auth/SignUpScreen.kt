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
    var role by remember { mutableStateOf("CUSTOMER") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val registerResult by viewModel.registerResult.collectAsState()
    val loginResult by viewModel.loginResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isAutoLoggingIn by remember { mutableStateOf(false) }

    LaunchedEffect(password, confirmPassword) {
        // Reset confirmPasswordError agar input tidak selalu merah saat mengetik password baru
        if (password != confirmPassword && password.isNotBlank() && confirmPassword.isNotBlank()) {
            // Biarkan error muncul saat validasi form
        } else if (password == confirmPassword && confirmPasswordError != null) {
            confirmPasswordError = null
        }
    }

    fun validateForm(): Boolean {
        var isValid = true

        viewModel.clearRegisterResult()
        viewModel.clearLoginResult()
        statusMessage = null

        // Cek Nama
        if (name.isBlank()) {
            nameError = "Nama lengkap tidak boleh kosong."
            isValid = false
        } else {
            nameError = null
        }

        // Cek Email
        if (email.isBlank()) {
            emailError = "Email tidak boleh kosong."
            isValid = false
        } else if (!isValidEmail(email)) {
            emailError = "Format email tidak valid."
            isValid = false
        } else {
            emailError = null
        }

        // Cek Password
        if (password.isBlank()) {
            passwordError = "Password tidak boleh kosong."
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Password minimal 6 karakter."
            isValid = false
        } else {
            passwordError = null
        }

        // Cek Konfirmasi Password
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

    val generalErrorMessage = if (nameError == null && emailError == null && passwordError == null && confirmPasswordError == null && statusMessage == null) {
        if (registerResult?.contains("success", true) == false || loginResult?.contains("success", true) == false) {
            (registerResult ?: loginResult)?.replace("\"", "")
        } else {
            null
        }
    } else {
        null
    }

    val context = LocalContext.current

    LaunchedEffect(registerResult) {
        registerResult?.let {
            if (it.contains("success", true)) {
                statusMessage = "Pendaftaran berhasil! Mencoba masuk..."
                delay(500L)
                isAutoLoggingIn = true
                viewModel.login(context, role, email, password)
            }
        }
    }

    LaunchedEffect(loginResult) {
        loginResult?.let {
            isAutoLoggingIn = false
            if (it.contains("success", true)) {
                statusMessage = "Berhasil masuk! Selamat datang."
                delay(1500L)
                onSignUpSuccess()
            } else if (it.isNotBlank() && !it.contains("success", true)) {
                statusMessage = "Gagal masuk otomatis: ${it.replace("\"", "")}"
                delay(2000L)
                statusMessage = null
                onLoginFailed()
            }
        }
    }

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
            Text(
                text = nameError!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp),
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- Email ---
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

        Spacer(modifier = Modifier.height(14.dp))

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
            Text(
                text = passwordError!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp),
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

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

        // --- Notifikasi Status (Sukses/Gagal Login Otomatis) ---
        if (statusMessage != null) {
            val isSuccess = statusMessage!!.contains("berhasil", ignoreCase = true) || statusMessage!!.contains("sukses", ignoreCase = true)
            Text(
                text = statusMessage!!.replace("\"", ""),
                color = if (isSuccess) Color(0xFF4CAF50) else Color.Red,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                textAlign = TextAlign.Center
            )
        }

        // --- Notifikasi Error API General ---
        generalErrorMessage?.let {
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


        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (!showLoading && validateForm()) {
                    viewModel.register(name, email, password)
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
                Text("Daftar", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

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

        Spacer(modifier = Modifier.height(24.dp))

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