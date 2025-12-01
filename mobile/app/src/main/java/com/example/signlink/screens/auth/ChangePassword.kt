package com.example.signlink.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.signlink.R
import com.example.signlink.ui.theme.SignLinkTeal
import com.example.signlink.ui.theme.DarkText
import com.example.signlink.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    onChangePasswordSuccess: () -> Unit,
) {
    val context = LocalContext.current
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var currentPasswordError by remember { mutableStateOf<String?>(null) }
    var newPasswordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val submitResult by viewModel.changePasswordSubmitResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    fun validateForm(): Boolean {
        var isValid = true
        currentPasswordError = null
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

    DisposableEffect(Unit) {
        viewModel.clearChangePasswordResult()

        onDispose {
            viewModel.clearChangePasswordResult()
        }
    }

    LaunchedEffect(submitResult) {
        submitResult?.let { result ->
            if (result.contains("success", ignoreCase = true)) {
                successMessage = "Kata Sandi Berhasil Dirubah!"
                delay(2000L)
                onChangePasswordSuccess()
            } else if (!result.contains("null", ignoreCase = true)) {
                successMessage = null
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = painterResource(id = R.drawable.signlink_logo),
                contentDescription = "SignLink Logo",
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Ubah Kata Sandi",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(text = "Kata Sandi Saat ini", modifier = Modifier.fillMaxWidth(), color = DarkText, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it; currentPasswordError = null },
                placeholder = { Text("Masukkan kata sandi saat ini") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (currentPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = "Toggle password visibility")
                    }
                },
                isError = currentPasswordError != null,
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
            if (currentPasswordError != null) {
                Text(
                    text = currentPasswordError!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp),
                    textAlign = TextAlign.Start
                )
            }
            Spacer(modifier = Modifier.height(14.dp))

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
                        viewModel.changePassword(context, currentPassword, newPassword)
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

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}