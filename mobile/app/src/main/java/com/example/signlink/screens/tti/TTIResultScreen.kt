package com.example.signlink.screens.tti

import android.os.Build
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.signlink.R
import com.example.signlink.components.DictionaryHeaderCard
import com.example.signlink.data.models.kamus.KamusData
import com.example.signlink.ui.theme.SignLinkTeal
import com.example.signlink.ui.theme.DarkText

private const val TRANSITION_DURATION = 500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TTIResultScreen(
    navController: NavController,
    data: List<KamusData>,
) {
    val context = LocalContext.current
    val baseUrl = if (isEmulator()) "http://10.0.2.2:7777/api/" else "http://10.0.2.2:7777/api/"

    val videoList = remember { data.map { it.url } }
    val wordList = remember { data.map { it.arti.replace("_", " ") } }

    var currentIndex by remember { mutableIntStateOf(0) }
    var showVideo by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(true) }

    val currentWord = wordList.getOrNull(currentIndex) ?: "Terjemahan"

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
        }
    }

    LaunchedEffect(currentIndex, videoList) {
        if (videoList.isNotEmpty()) {
            isLoading = true
            exoPlayer.pause()

            val videoUrl = videoList[currentIndex].removePrefix("/")
            val item = MediaItem.fromUri(baseUrl + videoUrl)

            exoPlayer.setMediaItem(item)
            exoPlayer.prepare()
            exoPlayer.play()
            isLoading = false
        }
    }

    DisposableEffect(Unit) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    if (currentIndex < videoList.size - 1) {
                        currentIndex++
                    } else {
                        exoPlayer.seekTo(0)
                        exoPlayer.pause()
                    }
                }
            }
        }

        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    val startFromBeginning: () -> Unit = {
        if (videoList.isNotEmpty()) {
            currentIndex = 0
        }
    }


    // UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Hasil Terjemahan",
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        showVideo = false
                        exoPlayer.stop()
                        exoPlayer.release()
                        navController.popBackStack()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.Black
                        )
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
                .verticalScroll(rememberScrollState())
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.signlink),
                contentDescription = "Logo",
                modifier = Modifier.size(80.dp)
            )

            Spacer(Modifier.height(16.dp))

            DictionaryHeaderCard(
                modifier = Modifier.fillMaxWidth(0.9f),
                title = "Terjemahan Kalimat",
                description = "Kalimat isyarat: ${wordList.joinToString(" ")}"
            )

            Spacer(Modifier.height(24.dp))

            if (showVideo) {
                Card(
                    modifier = Modifier.fillMaxWidth(0.95f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(Color.White),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    if (videoList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .height(250.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Tidak ada video")
                        }
                    } else {
                        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {

                            Crossfade(
                                targetState = currentIndex,
                                animationSpec = tween(TRANSITION_DURATION),
                                label = "VideoCrossfade"
                            ) { index ->
                                key(index) {
                                    AndroidView(
                                        factory = { ctx ->
                                            PlayerView(ctx).apply {
                                                player = exoPlayer
                                                useController = false
                                            }
                                        },
                                        update = { view ->
                                            if (view.player != exoPlayer) view.player = exoPlayer
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(250.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    exoPlayer.seekTo(0)
                                    exoPlayer.play()
                                },
                                modifier = Modifier.fillMaxWidth().height(60.dp),
                                colors = ButtonDefaults.buttonColors(SignLinkTeal)
                            ) {
                                Text(
                                    "$currentWord (${currentIndex + 1}/${videoList.size})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = startFromBeginning,
                enabled = videoList.size > 1 && currentIndex > 0,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = SignLinkTeal
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Mulai Ulang", modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Mulai dari Awal")
                }
            }
        }
    }
}


fun isEmulator(): Boolean {
    return (Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.lowercase().contains("vbox")
            || Build.FINGERPRINT.lowercase().contains("test-keys")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86"))
}