package com.example.signlink.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DotIndicator(isActive: Boolean) {
    Box(
        modifier = if (isActive) {
            Modifier
                .width(24.dp)
                .height(10.dp)
                .background(
                    color = Color(0xFF0094AE),
                    shape = RoundedCornerShape(50)
                )
        } else {
            Modifier
                .width(10.dp)
                .height(10.dp)
                .background(
                    color = Color.LightGray.copy(alpha = 0.5f),
                    shape = CircleShape
                )
        }
    )
}
