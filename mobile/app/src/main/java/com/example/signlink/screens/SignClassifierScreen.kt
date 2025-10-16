// SignClassifierScreen.kt
package com.example.signlink.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.YuvImage
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
import java.io.ByteArrayOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignClassifierScreen(navController: NavController) {
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
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
                    CameraContent()
                } else {
                    PermissionDeniedView(cameraPermissionLauncher)
                }
            }
        }
    )
}

@Composable
fun CameraContent() {
    val context = LocalContext.current
    val lifecycleOwner = LocalContext.current as LifecycleOwner
    var predictedLabel by remember { mutableStateOf("Menunggu gesture...") }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    val handsDetector = remember {
        HandsDetector(context, SignClassifier(context)) { label ->
            predictedLabel = label
        }
    }

    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            cameraProvider?.unbindAll()
            cameraExecutor.shutdown()
            handsDetector.close()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { ctx ->
            val previewView = PreviewView(ctx)
            startCamera(
                context = ctx,
                lifecycleOwner = lifecycleOwner,
                previewView = previewView,
                handsDetector = handsDetector,
                executor = cameraExecutor,
                onProviderReady = { provider -> cameraProvider = provider }
            )
            previewView
        }, modifier = Modifier.fillMaxSize())

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
                .background(Color(0xAA000000), RoundedCornerShape(16.dp))
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

@SuppressLint("UnsafeOptInUsageError")
private fun startCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    handsDetector: HandsDetector,
    executor: ExecutorService,
    onProviderReady: (ProcessCameraProvider) -> Unit
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()
        onProviderReady(cameraProvider)

        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(previewView.surfaceProvider)

        val cameraSelector = if (cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA))
            CameraSelector.DEFAULT_FRONT_CAMERA
        else CameraSelector.DEFAULT_BACK_CAMERA

        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalyzer.setAnalyzer(executor) { imageProxy ->
            val bitmap = imageProxy.toBitmap() ?: run {
                imageProxy.close()
                return@setAnalyzer
            }
            handsDetector.detect(bitmap)
            imageProxy.close()
        }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalyzer)
        } catch (e: Exception) {
            Log.e("CameraX", "Use case binding failed", e)
        }

    }, ContextCompat.getMainExecutor(context))
}

fun ImageProxy.toBitmap(): Bitmap? {
    if (format != ImageFormat.YUV_420_888) return null

    val yBuffer = planes[0].buffer
    val uBuffer = planes[1].buffer
    val vBuffer = planes[2].buffer
    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    return try {
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
        val out = ByteArrayOutputStream()
        val success = yuvImage.compressToJpeg(android.graphics.Rect(0, 0, width, height), 100, out)
        Log.i("CameraX", "JPEG compression success: $success, output bytes=${out.size()}")
        val imageBytes = out.toByteArray()
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        Log.i("CameraX", "Bitmap created: ${bitmap.width}x${bitmap.height}")
        bitmap
    } catch (e: Exception) {
        Log.e("CameraX", "toBitmap failed", e)
        null
    }
}
