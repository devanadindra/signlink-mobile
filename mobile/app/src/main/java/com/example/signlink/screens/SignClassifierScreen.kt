package com.example.signlink.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.example.signlink.data.signclassifier.HandsDetector
import com.example.signlink.data.signclassifier.SignClassifier
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignClassifierScreen(navController: NavController) {
    val context = LocalContext.current

    // State Izin Kamera
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Launcher untuk meminta izin
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasCameraPermission = isGranted
        }
    )

    // SideEffect: Minta izin saat Composable pertama kali masuk
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Penerjemah Gerakan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                if (hasCameraPermission) {
                    CameraContent(navController)
                } else {
                    PermissionDeniedView(cameraPermissionLauncher)
                }
            }
        }
    )
}

// --- Komponen yang Memegang Logika Kamera dan Cleanup ---

@Composable
fun CameraContent(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalContext.current as LifecycleOwner

    // State & Resources
    var predictedLabel by remember { mutableStateOf("Menunggu gesture...") }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val handsDetector = remember { HandsDetector(context, SignClassifier(context)) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }

    // Efek untuk cleanup: mematikan kamera dan executor saat layar keluar (PopBackStack)
    DisposableEffect(lifecycleOwner) {
        onDispose {
            Log.d("Cleanup", "Mematikan CameraX, menutup executor, dan detector.")
            cameraProvider?.unbindAll()
            cameraExecutor.shutdown()
            // Penting: Tutup resource ML
            handsDetector.close()
        }
    }

    // Camera Preview dan Overlay
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { ctx ->
            val previewView = PreviewView(ctx)

            // Panggil fungsi startCamera
            startCamera(
                context = ctx,
                lifecycleOwner = lifecycleOwner,
                previewView = previewView,
                handsDetector = handsDetector,
                executor = cameraExecutor,
                onPrediction = { label -> predictedLabel = label },
                onProviderReady = { provider -> cameraProvider = provider }
            )

            previewView
        }, modifier = Modifier.fillMaxSize())

        // Overlay prediksi gesture
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
                .background(Color(0xAA000000), shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = predictedLabel,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

// --- Komponen untuk View Izin Ditolak ---

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

// --- Fungsi Pendukung CameraX ---

/**
 * Fungsi startCamera menggunakan CameraX ImageAnalysis
 */
@SuppressLint("UnsafeOptInUsageError")
private fun startCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    handsDetector: HandsDetector,
    executor: ExecutorService,
    onPrediction: (String) -> Unit,
    onProviderReady: (ProcessCameraProvider) -> Unit
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()
        onProviderReady(cameraProvider)

        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(previewView.surfaceProvider)

        // Ubah ke DEFAULT_CAMERA atau DEFAULT_BACK_CAMERA jika kamera depan (DEFAULT_FRONT_CAMERA) gelap di emulator
        val cameraSelector = if (
            cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
        ) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }


        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalyzer.setAnalyzer(executor) { imageProxy ->
            val bitmap = imageProxy.toBitmap() ?: run {
                imageProxy.close()
                return@setAnalyzer
            }

            val label = handsDetector.detect(bitmap)
            onPrediction(label.toString())

            imageProxy.close()
        }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalyzer
            )
        } catch (e: Exception) {
            // Ini akan log error jika binding CameraX gagal (misal, tidak ada izin)
            Log.e("CameraX", "Use case binding failed", e)
        }

    }, ContextCompat.getMainExecutor(context))
}

/**
 * Extension function: konversi ImageProxy -> Bitmap
 */
fun ImageProxy.toBitmap(): Bitmap? {
    val planeProxy = planes.firstOrNull() ?: return null
    val buffer = planeProxy.buffer
    val bytes = ByteArray(buffer.capacity())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}