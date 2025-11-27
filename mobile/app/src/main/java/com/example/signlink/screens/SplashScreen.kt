package com.example.signlink.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.signlink.R
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
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .clipToBounds()
                .graphicsLayer {
                    clip = true
                    shape = RectangleShape
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.signlink_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }
}
