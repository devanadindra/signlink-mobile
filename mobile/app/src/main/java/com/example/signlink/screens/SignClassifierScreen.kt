package com.example.signlink.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.signlink.data.signclassifier.HandsDetector
import java.util.concurrent.Executors
import android.util.Log
import android.util.Size
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun checkCameraAvailability(context: Context): Pair<Boolean, Boolean> {
    return try {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        val frontAvailable = cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
        val backAvailable = cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)
        Pair(frontAvailable, backAvailable)
    } catch (_: Exception) {
        Pair(false, false)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignClassifierScreen(navController: NavController) {
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasCameraPermission = isGranted }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Penerjemah Bahasa Isyarat") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (hasCameraPermission) {
                CameraContent()
            } else {
                PermissionDeniedView(cameraPermissionLauncher)
            }
        }
    }
}

@Suppress("DEPRECATION")
@SuppressLint("DefaultLocale")
@Composable
fun CameraContent() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var label by remember { mutableStateOf("Menunggu gesture...") }
    var confidence by remember { mutableFloatStateOf(0f) }
    var detectedWord by remember { mutableStateOf("") }

    var lastLabel by remember { mutableStateOf("") }
    var stableCount by remember { mutableIntStateOf(0) }
    val stabilityThreshold = 5

    var isCooldownActive by remember { mutableStateOf(false) }
    val cooldownDuration = 800L

    var lastDetectedGesture by remember { mutableStateOf<String?>(null) }
    var noHandDetectionStartTime by remember { mutableStateOf<Long?>(null) }

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var isFrontCameraActive by remember { mutableStateOf(true) }
    var isSwitchButtonVisible by remember { mutableStateOf(false) }

    var detector by remember { mutableStateOf<HandsDetector?>(null) }
    var cameraProviderInstance by remember { mutableStateOf<ProcessCameraProvider?>(null) }

    var tts: TextToSpeech? by remember { mutableStateOf(null) }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            cameraProviderInstance?.unbindAll()
            cameraExecutor.shutdown()
            tts?.stop()
            tts?.shutdown()
        }
    }

    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale("id", "ID"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Bahasa tidak didukung.")
                }
            } else {
                Log.e("TTS", "Inisialisasi TTS gagal.")
            }
        }
    }

    LaunchedEffect(Unit) {
        val (front, back) = checkCameraAvailability(context)
        when {
            front && back -> {
                isSwitchButtonVisible = true
                isFrontCameraActive = true
            }
            back -> isFrontCameraActive = false
            front -> isFrontCameraActive = true
            else -> Log.e("CameraContent", "No usable camera found!")
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(100)

            if (detectedWord.isEmpty()) {
                noHandDetectionStartTime = null
                continue
            }

            if (lastDetectedGesture == "No Hand") {
                val now = System.currentTimeMillis()
                if (noHandDetectionStartTime == null) {
                    noHandDetectionStartTime = now
                } else if (now - noHandDetectionStartTime!! >= 1000) {
                    if (!detectedWord.endsWith(" ")) {
                        detectedWord += " "
                        Log.i("SignClassifier", "Space added after 1s 'No Hand'")
                    }
                    noHandDetectionStartTime = null
                }
            } else {
                noHandDetectionStartTime = null
            }
        }
    }


    val previewView = remember { PreviewView(context) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    LaunchedEffect(isFrontCameraActive) {
        cameraProviderFuture.addListener({
            val provider = cameraProviderFuture.get()
            cameraProviderInstance = provider
            val selector = if (isFrontCameraActive) CameraSelector.DEFAULT_FRONT_CAMERA
            else CameraSelector.DEFAULT_BACK_CAMERA

            provider.unbindAll()

            detector = HandsDetector(
                context = context,
                onGestureDetected = { gesture, conf ->
                    lastDetectedGesture = gesture

                    if (!isCooldownActive) {
                        label = gesture
                        confidence = conf

                        if (gesture != "No Hand" && gesture.length == 1) {
                            if (gesture == lastLabel) {
                                stableCount++
                                if (stableCount >= stabilityThreshold) {
                                    detectedWord += gesture

                                    isCooldownActive = true
                                    stableCount = 0
                                    lastLabel = ""

                                    coroutineScope.launch {
                                        delay(cooldownDuration)
                                        isCooldownActive = false
                                    }
                                }
                            } else {
                                lastLabel = gesture
                                stableCount = 1
                            }
                        } else {
                            lastLabel = ""
                            stableCount = 0
                        }
                    }
                },
                isFrontCamera = isFrontCameraActive
            )

            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }
            val analyzer = ImageAnalysis.Builder()
                .setTargetResolution(Size(640, 480))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                        detector?.detect(imageProxy)
                    }
                }

            try {
                provider.bindToLifecycle(lifecycleOwner, selector, preview, analyzer)
            } catch (e: Exception) {
                Log.e("CameraX", "Binding failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(modifier = Modifier.align(Alignment.TopCenter)) {
            Text(
                text = if (label == "No Hand") "Tidak ada tangan"
                else "$label (${String.format("%.2f", confidence)})",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Hasil Kata:", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
                Text(
                    text = if (detectedWord.isEmpty())
                        "Belum ada huruf terdeteksi"
                    else detectedWord + "\uFEFF",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                if (detectedWord.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                tts?.speak(detectedWord, TextToSpeech.QUEUE_FLUSH, null, null)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF2E7D32)
                            ),
                            shape = RoundedCornerShape(50)
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Baca Kata")
                            Spacer(Modifier.width(4.dp))
                            Text("Baca", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { detectedWord = "" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.9f),
                                contentColor = Color(0xFFB71C1C)
                            ),
                            shape = RoundedCornerShape(50)
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = "Hapus Kata")
                            Spacer(Modifier.width(4.dp))
                            Text("Hapus", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (isSwitchButtonVisible) {
            FloatingActionButton(
                onClick = { isFrontCameraActive = !isFrontCameraActive },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(y = (-100).dp)
            ) {
                Icon(Icons.Default.Cameraswitch, contentDescription = "Ganti Kamera")
            }
        }
    }
}


@Composable
fun PermissionDeniedView(launcher: androidx.activity.compose.ManagedActivityResultLauncher<String, Boolean>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(24.dp)
    ) {
        Text(
            "Akses kamera diperlukan untuk menerjemahkan gerakan.",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
            Text("Minta Izin Kamera")
        }
    }
}