package com.example.signlink.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.signlink.ui.theme.DarkText
import com.example.signlink.ui.theme.SignLinkTeal

@Composable
fun EditProfileForm(
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    isEmailDisabled: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        // --- Input Nama ---
        Text(
            text = "Nama Lengkap",
            modifier = Modifier.fillMaxWidth(),
            color = DarkText,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            placeholder = { Text("Masukkan nama lengkap Anda") },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = "Ikon Nama", tint = Color.LightGray)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = SignLinkTeal,
                unfocusedBorderColor = Color.LightGray,
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Input Email ---
        Text(
            text = "Email",
            modifier = Modifier.fillMaxWidth(),
            color = DarkText,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = { Text("Masukkan alamat email Anda") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = "Ikon Email", tint = if (isEmailDisabled) Color.Gray else Color.LightGray)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isEmailDisabled,
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = if (isEmailDisabled) Color.Gray else Color.Black,
                disabledTextColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color(0xFFF0F0F0),
                focusedBorderColor = SignLinkTeal,
                unfocusedBorderColor = if (isEmailDisabled) Color.LightGray.copy(alpha = 0.5f) else Color.LightGray,
                disabledBorderColor = Color.LightGray.copy(alpha = 0.5f)
            )
        )

        if (isEmailDisabled) {
            Text(
                text = "Email tidak dapat diubah untuk akun yang login via Google",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp),
            )
        }
    }
}