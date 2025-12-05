package com.example.signlink.screens.kamus

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.signlink.components.VideoPlayer
import com.example.signlink.ui.theme.DarkText
import com.example.signlink.ui.theme.SignLinkTeal
import androidx.media3.exoplayer.ExoPlayer
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.composed
import kotlinx.coroutines.delay


@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.shimmerEffect(): Modifier = composed {
    background(Color(0xFFE0E0E0))
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KamusDetailScreen(
    navController: NavController,
    arti: String,
    videoUrl: String? = null
) {
    var player: ExoPlayer? by remember { mutableStateOf(null) }
    var showVideo by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(true) }


    LaunchedEffect(key1 = true) {
        delay(1000)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = arti, fontWeight = FontWeight.Bold, color = DarkText) },
                navigationIcon = {
                    IconButton(onClick = {
                        showVideo = false
                        player?.stop()
                        player?.release()
                        player = null
                        navController.popBackStack()
                    }) {
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // 1. Logo
            Image(
                painter = painterResource(id = R.drawable.signlink),
                contentDescription = "SignLink Logo",
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Header Card
            DictionaryHeaderCard(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(horizontal = 8.dp),
                title = "Kamus Bahasa Isyarat BISINDO",
                description = "Kumpulan Bahasa Isyarat dan terjemahannya"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Konten Detail
            if (showVideo) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    if (isLoading) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                                    .shimmerEffect()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .shimmerEffect()
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            VideoPlayer(
                                videoUrl = videoUrl ?: "",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp),
                                onPlayerReady = { p ->
                                    player = p
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    println("Detail untuk kata: $arti")
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SignLinkTeal,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                            ) {
                                Text(
                                    text = arti,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}