package com.example.signlink.data.signclassifier

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker.HandLandmarkerOptions
import androidx.core.graphics.createBitmap

class HandsDetector(
    private val context: Context,
    private val onGestureDetected: (String, Float) -> Unit,
    private val isFrontCamera: Boolean = false
) {

    private val mainHandler = Handler(Looper.getMainLooper())
    private val signClassifier = SignClassifier(context)
    private val yuvConverter = YuvToRgbConverter()

    var handCount: Int = 0
        private set

    private val handLandmarker: HandLandmarker by lazy {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath("hand_landmarker.task")
            .build()

        val options = HandLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setNumHands(2)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setResultListener { result, _ ->
                handleLandmarkResult(result.landmarks())
            }
            .setErrorListener { error ->
                Log.e("HandsDetector", "LiveStream error: ${error.message}")
            }
            .build()

        HandLandmarker.createFromOptions(context, options)
    }

    fun detect(imageProxy: ImageProxy) {
        try {
            val bitmap = createBitmap(imageProxy.width, imageProxy.height)
            yuvConverter.yuvToRgb(imageProxy, bitmap)

            val matrix = Matrix().apply {
                postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
                if (isFrontCamera) {
                    postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
                }
            }

            val rotatedBitmap =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

            val mpImage = BitmapImageBuilder(rotatedBitmap).build()
            handLandmarker.detectAsync(mpImage, System.currentTimeMillis())

        } catch (e: Exception) {
            Log.e("HandsDetector", "Detect error: ${e.message}", e)
        } finally {
            imageProxy.close()
        }
    }

    private fun handleLandmarkResult(allHands: List<List<NormalizedLandmark>?>?) {
        handCount = allHands?.count { !it.isNullOrEmpty() } ?: 0

        if (handCount == 0) {
            mainHandler.post { onGestureDetected("No Hand", 0f) }
            return
        }

        Log.d("HandsDetector", "Jumlah tangan terdeteksi: $handCount")

        val keypoints = extractTwoHandsKeypoints(allHands!!)

        val (label, confidence) = signClassifier.predict(keypoints)

        val isTwoHandGesture = when (label) {
            "A", "B", "D", "G", "H", "T", "K", "M", "N", "P", "Q", "S", "W", "X", "Y" -> true
            else -> false
        }

        if (isTwoHandGesture && handCount < 2) {
            Log.d("HandsDetector", "Gesture $label diabaikan karena butuh dua tangan (terdeteksi $handCount).")
            return
        }

        if (confidence > 0.6f) {
            mainHandler.post { onGestureDetected(label, confidence) }
        } else {
            Log.d("HandsDetector", "Gesture diabaikan karena confidence rendah: $confidence ($label)")
        }
    }

    private fun extractTwoHandsKeypoints(allHands: List<List<NormalizedLandmark>?>): FloatArray {
        val maxHands = 2
        val landmarksPerHand = 21
        val coordsPerLandmark = 3
        val totalSize = maxHands * landmarksPerHand * coordsPerLandmark
        val result = FloatArray(totalSize)

        for (i in 0 until maxHands) {
            val hand = allHands.getOrNull(i)
            if (!hand.isNullOrEmpty()) {
                for (j in hand.indices) {
                    val lm = hand[j]
                    val base = i * landmarksPerHand * coordsPerLandmark + j * coordsPerLandmark
                    val x = if (isFrontCamera) 1f - lm.x() else lm.x()
                    result[base] = x
                    result[base + 1] = lm.y()
                    result[base + 2] = lm.z()
                }
            }
        }
        return result
    }
}
