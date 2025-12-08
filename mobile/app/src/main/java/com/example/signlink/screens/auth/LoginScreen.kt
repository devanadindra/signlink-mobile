package com.example.signlink.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import com.example.signlink.ui.theme.SignLinkTeal
import com.example.signlink.ui.theme.DarkText
import com.example.signlink.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onSignUpClicked: () -> Unit,
    onForgotPasswordClicked: () -> Unit,
    onGoogleAuth: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    var role by remember { mutableStateOf("CUSTOMER") }
    var tapCount by remember { mutableIntStateOf(0) }
    var lastTapTime by remember { mutableLongStateOf(0L) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    var roleChangeMessage by remember { mutableStateOf<String?>(null) }

    val isLoading by viewModel.isLoading.collectAsState()
    val loginComplete by viewModel.loginComplete.collectAsState()
    val context = LocalContext.current

    fun validateForm(): Boolean {
        var isValid = true

        emailError = null
        passwordError = null

        if (email.isBlank()) {
            emailError = "Email tidak boleh kosong."
            isValid = false
        } else if (!isValidEmail(email)) {
            emailError = "Format email tidak valid."
            isValid = false
        }

        if (password.isBlank()) {
            passwordError = "Password tidak boleh kosong."
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Password minimal 6 karakter."
            isValid = false
        }

        return isValid
    }

    LaunchedEffect(Unit) {
        viewModel.clearAll()
        launch {
            viewModel.successMessage.collectLatest { success ->
                success?.let {
                    delay(1500)
                    Toast.makeText(context, "Berhasil: $it", Toast.LENGTH_SHORT).show()

                    if (loginComplete) {
                        onLoginSuccess()
                    }

                    viewModel.clearSuccess()
                }
            }
        }

        launch {
            viewModel.errorMessage.collectLatest { error ->
                error?.let {
                    delay(1500)
                    Toast.makeText(context, "Gagal: $it", Toast.LENGTH_LONG).show()
                    viewModel.clearError()
                }
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

        Text(
            text = "Selamat Datang Kembali!",
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

        Spacer(modifier = Modifier.height(40.dp))

        Text(text = "Email", modifier = Modifier.fillMaxWidth(), color = DarkText, fontWeight = FontWeight.Medium)
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

        Spacer(modifier = Modifier.height(14.dp))

        Text(text = "Password", modifier = Modifier.fillMaxWidth(), color = DarkText, fontWeight = FontWeight.Medium)
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
            isError = passwordError != null,
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

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(checkedColor = SignLinkTeal)
                )
                Text("Ingat Saya", fontSize = 14.sp, color = DarkText)
            }
            Text(
                text = "Lupa Kata Sandi?",
                fontSize = 14.sp,
                color = SignLinkTeal,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onForgotPasswordClicked)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                if (!isLoading && validateForm()) {
                    viewModel.login(context, role, email, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = DarkText,
                containerColor = SignLinkTeal,
                disabledContentColor = Color.Gray,
                disabledContainerColor = Color(0xFFE0E0E0),
            ),
            shape = RoundedCornerShape(50),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Masuk", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
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
            onClick = { onGoogleAuth() },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(50)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = DarkText,
                containerColor = Color.White,
                disabledContentColor = Color.Gray,
                disabledContainerColor = Color(0xFFE0E0E0),
            ),
            shape = RoundedCornerShape(50)
        )
 {
            Image(
                painter = painterResource(id = R.drawable.google),
                contentDescription = "Google Logo",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("Lanjutkan dengan Google", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(40.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Belum memiliki akun? ",
                color = Color.Gray,
                fontSize = 16.sp
            )

            Text(
                text = "Daftar",
                color = if (isLoading) Color.Gray else SignLinkTeal,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .alpha(if (isLoading) 0.5f else 1f)
                    .clickable(
                        enabled = !isLoading,
                        onClick = onSignUpClicked
                    )
            )
        }


        Spacer(modifier = Modifier.height(32.dp))
    }
}