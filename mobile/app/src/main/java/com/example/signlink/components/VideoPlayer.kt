package com.example.signlink.components

import android.widget.FrameLayout
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun VideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val baseUrl = remember {
        if (isEmulator()) {
            "http://10.0.2.2:7777/api/"
        } else {
            "http://10.39.49.67:7777/api/"
        }
    }

    val exoPlayer = remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            val cleanUrl = videoUrl.removePrefix("/")
            val item = MediaItem.fromUri(baseUrl + cleanUrl)
            setMediaItem(item)
            prepare()
            playWhenReady = false
        }
    }


    var isPlaying by remember { mutableStateOf(false) }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    isPlaying = false
                    exoPlayer.seekTo(0)
                    exoPlayer.pause()
                }
            }
        }

        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    Column(modifier = modifier) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    )
                }
            },
            update = { view ->
                if (view.player != exoPlayer) view.player = exoPlayer
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                val newPosition = (exoPlayer.currentPosition - 2_000).coerceAtLeast(0)
                exoPlayer.seekTo(newPosition)
            }) {
                Icon(
                    imageVector = Icons.Filled.FastRewind,
                    contentDescription = "Rewind 2s",
                    tint = Color.Black
                )
            }

            IconButton(onClick = {
                if (exoPlayer.isPlaying) {
                    exoPlayer.pause()
                    isPlaying = false
                } else {
                    exoPlayer.play()
                    isPlaying = true
                }
            }) {
                Icon(
                    imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(48.dp),
                    tint = Color.Black
                )
            }

            IconButton(onClick = {
                val newPosition = (exoPlayer.currentPosition + 2_000).coerceAtMost(exoPlayer.duration)
                exoPlayer.seekTo(newPosition)
            }) {
                Icon(
                    imageVector = Icons.Filled.FastForward,
                    contentDescription = "Forward 2s",
                    tint = Color.Black
                )
            }
        }
    }
}

fun isEmulator(): Boolean {
    return (android.os.Build.FINGERPRINT.startsWith("generic")
            || android.os.Build.FINGERPRINT.lowercase().contains("vbox")
            || android.os.Build.FINGERPRINT.lowercase().contains("test-keys")
            || android.os.Build.MODEL.contains("Emulator")
            || android.os.Build.MODEL.contains("Android SDK built for x86"))
}
