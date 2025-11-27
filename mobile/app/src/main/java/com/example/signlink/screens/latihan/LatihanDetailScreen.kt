@file:Suppress("DEPRECATION")

package com.example.signlink.screens.latihan

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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.CheckCircle
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
import java.util.*
import java.util.concurrent.Executors
import android.util.Log
import android.util.Size
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import com.example.signlink.Destinations
import com.example.signlink.screens.PermissionDeniedView
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URLDecoder

data class LatihanResult(
    val totalCharacters: Int,
    val totalTimeSeconds: Long
)

private fun getCharactersFromJson(jsonString: String): List<String> {
    return try {
        val decoded = URLDecoder.decode(jsonString, "UTF-8")

        if (decoded.trim().startsWith("[")) {
            val listType = object : TypeToken<List<String>>() {}.type
            return Gson().fromJson(decoded, listType)
        }

        return listOf(decoded.trim())

    } catch (e: Exception) {
        Log.e("LatihanDetail", "Error decoding characters: ${e.message}")
        emptyList()
    }
}


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
fun LatihanDetailScreen(navController: NavController,
                        charactersJson: String) {
    val context = LocalContext.current
    val practiceCharacters = remember { getCharactersFromJson(charactersJson) }

    val startTime = remember { System.currentTimeMillis() }

    var currentWordIndex by rememberSaveable { mutableIntStateOf(0) }
    var currentLetterIndex by rememberSaveable { mutableIntStateOf(0) }

    val currentWord = practiceCharacters.getOrNull(currentWordIndex)
    val letters = currentWord?.toCharArray()?.map { it.toString() } ?: emptyList()
    val currentLetter = letters.getOrNull(currentLetterIndex)

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
                title = { Text("Latihan Bahasa Isyarat") },
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
                if (currentLetter != null) {
                    PracticeCameraContent(
                        currentWord = currentWord ?: "",
                        currentTarget = currentLetter,
                        currentLetterIndex = currentLetterIndex,
                        onCorrectSign = {
                            if (currentLetterIndex < letters.lastIndex) {
                                currentLetterIndex++
                            } else {
                                currentLetterIndex = 0

                                if (currentWordIndex < practiceCharacters.lastIndex) {
                                    currentWordIndex++
                                } else {
                                    val endTime = System.currentTimeMillis()
                                    val totalTimeSeconds = (endTime - startTime) / 1000
                                    val result = LatihanResult(
                                        totalCharacters = practiceCharacters.size,
                                        totalTimeSeconds = totalTimeSeconds
                                    )

                                    val resultJson = Gson().toJson(result)
                                    val encodedResult =
                                        java.net.URLEncoder.encode(resultJson, "UTF-8")

                                    navController.navigate("${Destinations.LATIHAN_RESULT_SCREEN}/$encodedResult")
                                }
                            }
                        }
                    )
                } else {
                    Text(
                        "Latihan Selesai! 🎉",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
            } else {
                PermissionDeniedView(cameraPermissionLauncher)
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun PracticeCameraContent(
    currentWord: String,
    currentTarget: String,
    currentLetterIndex: Int,
    onCorrectSign: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var label by remember { mutableStateOf("Menunggu isyarat...") }
    var confidence by remember { mutableFloatStateOf(0f) }
    var isSignCorrect by remember { mutableStateOf(false) }
    var isCooldownActive by remember { mutableStateOf(false) }

    var lastLabel by remember { mutableStateOf("") }
    var stableCount by remember { mutableIntStateOf(0) }
    val stabilityThreshold = 5
    val cooldownDuration = 1500L

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

    LaunchedEffect(currentWord, currentTarget) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("id", "ID")
                if (currentWord.length > 1 && currentLetterIndex == 0) {
                    tts?.speak("Kata: $currentWord. Isyarat untuk huruf $currentTarget", TextToSpeech.QUEUE_FLUSH, null, null)
                } else {
                    tts?.speak("Isyarat untuk $currentTarget", TextToSpeech.QUEUE_FLUSH, null, null)
                }
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
            else -> Log.e("PracticeContent", "No usable camera found!")
        }
    }

    val previewView = remember { PreviewView(context) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    LaunchedEffect(isFrontCameraActive, currentTarget) {
        cameraProviderFuture.addListener({
            val provider = cameraProviderFuture.get()
            cameraProviderInstance = provider
            val selector = if (isFrontCameraActive) CameraSelector.DEFAULT_FRONT_CAMERA
            else CameraSelector.DEFAULT_BACK_CAMERA

            provider.unbindAll()

            detector = HandsDetector(
                context = context,
                onGestureDetected = { gesture, conf ->
                    if (isCooldownActive) return@HandsDetector

                    label = gesture
                    confidence = conf

                    if (gesture == currentTarget) {
                        if (gesture == lastLabel) {
                            stableCount++
                            if (stableCount >= stabilityThreshold) {
                                isSignCorrect = true
                                isCooldownActive = true
                                stableCount = 0
                                lastLabel = ""

                                tts?.speak("Benar!", TextToSpeech.QUEUE_FLUSH, null, null)

                                coroutineScope.launch {
                                    delay(cooldownDuration)
                                    onCorrectSign()
                                    isSignCorrect = false
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
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Kata Target:",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    currentWord.forEachIndexed { index, char ->
                        val charStr = char.toString()
                        val isCompleted = index < currentLetterIndex
                        val isCurrent = index == currentLetterIndex

                        TargetLetterBox(
                            letter = charStr,
                            isCompleted = isCompleted,
                            isCurrent = isCurrent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                IconButton(
                    onClick = {
                        tts?.speak("Isyarat untuk $currentTarget", TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = "Ulangi Target", tint = Color.White)
                }
            }
        }

        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Deteksi:", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
                Text(
                    text = if (label == "No Hand") "Tidak ada tangan"
                    else "$label (${String.format("%.2f", confidence)})",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        AnimatedVisibility(
            visible = isSignCorrect,
            enter = fadeIn(animationSpec = tween(500)),
            exit = fadeOut(animationSpec = tween(500)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2E7D32).copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Benar",
                    tint = Color.White,
                    modifier = Modifier.size(150.dp)
                )
            }
        }

        if (isSwitchButtonVisible) {
            FloatingActionButton(
                onClick = { isFrontCameraActive = !isFrontCameraActive },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-16).dp, y = (-16).dp),
                containerColor = Color.White
            ) {
                Icon(Icons.Default.Cameraswitch, contentDescription = "Ganti Kamera")
            }
        }
    }
}

@Composable
fun TargetLetterBox(letter: String, isCompleted: Boolean, isCurrent: Boolean) {
    val backgroundColor = when {
        isCompleted -> Color(0xFF81C784)
        isCurrent -> Color.White.copy(alpha = 0.9f)
        else -> Color.White.copy(alpha = 0.3f)
    }

    val textColor = when {
        isCompleted -> Color.White
        isCurrent -> Color.Black
        else -> Color.White
    }

    Card(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .size(48.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter,
                color = textColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}