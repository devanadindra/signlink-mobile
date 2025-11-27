package com.example.signlink.screens.latihan

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.signlink.Destinations
import androidx.compose.runtime.remember
import com.google.gson.Gson
import java.net.URLDecoder

private fun getResultFromJson(jsonString: String): LatihanResult? {
    return try {
        val decodedJson = URLDecoder.decode(jsonString, "UTF-8")
        Gson().fromJson(decodedJson, LatihanResult::class.java)
    } catch (_: Exception) {
        null
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun LatihanResultScreen(
    navController: NavController,
    resultJson: String
) {
    val result = remember { getResultFromJson(resultJson) }

    val totalChars = result?.totalCharacters ?: 0
    val totalTime = result?.totalTimeSeconds ?: 0L

    val averageSpeed = if (totalChars > 0 && totalTime > 0) {
        String.format("%.1f", totalTime.toFloat() / totalChars)
    } else {
        "N/A"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            Icon(
                Icons.Default.CheckCircle,
                contentDescription = "Selesai",
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Latihan Selesai!",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Anda telah berhasil menyelesaikan semua isyarat dalam modul ini. Kerja bagus!",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray.copy(alpha = 0.8f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    StatRow(
                        label = "Total Isyarat",
                        value = totalChars.toString()
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color(0xFFC8E6C9)
                    )
                    StatRow(
                        label = "Kecepatan Rata-rata",
                        value = "$averageSpeed detik/isyarat"
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))


            Button(
                onClick = {
                    navController.popBackStack(route = Destinations.LATIHAN_SCREEN, inclusive = false)
                },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Kembali ke Daftar Modul",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color.Black.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}