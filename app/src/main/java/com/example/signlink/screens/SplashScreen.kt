package com.example.signlink.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.signlink.R
import com.example.signlink.ui.theme.SignLinkTeal
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    // 1. Logika Jeda Waktu (Coroutine)
    LaunchedEffect(key1 = true) {
        delay(3000L) // Jeda 3 detik
        onTimeout()  // Panggil fungsi navigasi setelah jeda
    }

    // 2. Tampilan UI (Sesuai Gambar)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SignLinkTeal), // Warna #0094AE
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.signlink),
            contentDescription = "SignLink Logo",
            modifier = Modifier.size(200.dp)
        )
    }
}