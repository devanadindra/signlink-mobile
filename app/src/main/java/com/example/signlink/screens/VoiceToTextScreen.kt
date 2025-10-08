package com.example.signlink.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.signlink.R
import com.example.signlink.components.BottomBarSignLink
import com.example.signlink.components.MainFloatingActionButton
import com.example.signlink.components.NavItem
import com.example.signlink.ui.theme.*
import androidx.core.content.ContextCompat

// Definisikan warna yang digunakan (disusun ulang)
val DarkText = Color(0xFF1E1E1E)
val LightGrayBackground = Color(0xFFE0E0E0) // Warna abu-abu pucat untuk area teks
val CardBackground = Color.White // Tambahkan definisi warna Card

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceToTextScreen(
    onHomeClicked: () -> Unit = {},
    onKamusClicked: () -> Unit = {},
    onVTTClicked: () -> Unit = {},
    onProfileClicked: () -> Unit = {},
    onCameraClicked: () -> Unit = {}
) {
    val context = LocalContext.current
    var recognizedText by remember { mutableStateOf("Teks hasil rekaman akan muncul di sini...") }
    var isRecording by remember { mutableStateOf(false) }
    var isIndonesian by remember { mutableStateOf(true) }

    // SpeechRecognizer dibuat sekali
    val speechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() { isRecording = false }
                override fun onError(error: Int) {
                    recognizedText = "Terjadi kesalahan: Suara / Bahasa tidak dikenali"
                    isRecording = false
                }
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) recognizedText = matches[0]
                    isRecording = false
                }
                override fun onPartialResults(partialResults: Bundle?) {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) recognizedText = matches[0]
                }
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) recognizedText = "Izin mikrofon ditolak"
        }

    val navItems = listOf(
        NavItem("Beranda", Icons.Default.Home, false, "home"),
        NavItem("Kamus", Icons.Default.Book, false, "kamus"),
        NavItem("Penerjemah", Icons.Default.Camera, false, "penerjemah"),
        NavItem("VTT", Icons.Default.Mic, true, "vtt"),
        NavItem("Profil", Icons.Default.Person, false, "profil")
    )

    Scaffold(
        bottomBar = {
            BottomBarSignLink(
                items = navItems,
                onHomeClicked = onHomeClicked,
                onKamusClicked = onKamusClicked,
                onVTTClicked = onVTTClicked,
                onProfileClicked = onProfileClicked
            )
        },
        floatingActionButton = { MainFloatingActionButton(onClick = onCameraClicked) },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(72.dp))

            // 1. Logo
            Image(
                painter = painterResource(id = R.drawable.signlink),
                contentDescription = "SignLink Logo",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Deskripsi Card
            VttDescriptionCard(
                modifier = Modifier.fillMaxWidth(0.9f).padding(horizontal = 8.dp),
                subtitle = "Suara ke Tulisan",
                description = "Mulailah berbicara untuk mengubah suara menjadi teks"
            )

            Spacer(modifier = Modifier.height(24.dp)) // Jarak antara Card dan Toggle

            // BARU: Toggle Pilih Bahasa
            LanguageToggle(
                isIndonesian = isIndonesian,
                onToggle = { isIndonesian = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Text hasil rekaman
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LightGrayBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(300.dp)
                    .padding(horizontal = 8.dp)
            ) {
                val scrollState = rememberScrollState()
                Text(
                    text = recognizedText,
                    color = DarkText,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                )
            }

            // 4. Tombol rekam
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(horizontal = 8.dp)
                    .height(60.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        if (!isRecording) {
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                return@FloatingActionButton
                            }

                            val language = if (isIndonesian) "id-ID" else "en-US"

                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                            }

                            speechRecognizer.startListening(intent)
                            recognizedText = "Sedang mendengarkan..."
                            isRecording = true
                        } else {
                            speechRecognizer.stopListening()
                            isRecording = false
                        }
                    },
                    containerColor = if (isRecording) Color.Red else SignLinkTeal,
                    shape = RoundedCornerShape(50),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-30).dp)
                ) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = if (isRecording) "Stop Rekam" else "Mulai Rekam",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}

/**
 * Komponen Toggle Button untuk memilih Bahasa (Indonesia/English).
 */
@Composable
fun LanguageToggle(
    isIndonesian: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        // Tombol Indonesia
        Button(
            onClick = { onToggle(true) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isIndonesian) SignLinkTeal else CardBackground,
                contentColor = if (isIndonesian) Color.White else DarkText
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text("Indonesia", fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Tombol English
        Button(
            onClick = { onToggle(false) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (!isIndonesian) SignLinkTeal else CardBackground,
                contentColor = if (!isIndonesian) Color.White else DarkText
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text("English", fontWeight = FontWeight.SemiBold)
        }
    }
}


@Composable
fun VttDescriptionCard(
    modifier: Modifier = Modifier,
    subtitle: String,
    description: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp).fillMaxWidth()
        ) {
            Text(
                text = subtitle,
                color = DarkText,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
