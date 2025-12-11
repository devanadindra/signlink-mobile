package com.example.signlink.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.signlink.ui.theme.SignLinkTeal
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {

    LaunchedEffect(Unit) {
        delay(3000L)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SignLinkTeal),
        contentAlignment = Alignment.Center
    ) {}
}
