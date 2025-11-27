package com.example.signlink.screens.kuis

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.signlink.R
import com.example.signlink.components.DictionaryHeaderCard
import com.example.signlink.ui.theme.SignLinkTeal
import com.example.signlink.Destinations

data class KuisModul(
    val title: String,
    val totalSoal: Int,
    val timeLimitMinutes: Int,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KuisScreen(
    navController: NavController
) {
    val kuisModules = listOf(
        KuisModul("Abjad Dasar", 5, 10, "kuis_start/abjad"),
        KuisModul("Kata Dasar Batch 1", 5, 10, "kuis_start/kata_dasar_1"),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
        ) {
            item {
                Image(
                    painter = painterResource(id = R.drawable.signlink),
                    contentDescription = "SignLink Logo",
                    modifier = Modifier.size(80.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                DictionaryHeaderCard(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(horizontal = 8.dp),
                    title = "Kuis Singkat SignLink",
                    description = "Uji kemampuan Anda dalam Bahasa Isyarat BISINDO"
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            items(kuisModules) { modul ->
                KuisModulCard(
                    modul = modul,
                    onClick = {
                        val quizId = modul.route.substringAfterLast("/")
                        navController.navigate("${Destinations.KUIS_DETAIL_SCREEN}/$quizId")
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

/**
 * Komponen Card untuk setiap modul kuis (Abjad Dasar, Kata Dasar A, dll.)
 */
@Composable
fun KuisModulCard(
    modul: KuisModul,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = SignLinkTeal),
        modifier = modifier
            .fillMaxWidth(0.9f)
            .height(90.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ListAlt,
                    contentDescription = "Ikon Kuis",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = modul.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.AutoMirrored.Filled.ListAlt,
                        contentDescription = "Total Soal",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${modul.totalSoal} Soal",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = "Batas Waktu",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${modul.timeLimitMinutes} Menit",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}
