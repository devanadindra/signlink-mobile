// HandsDetector.kt
package com.example.signlink.data.signclassifier

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker.HandLandmarkerOptions
import com.google.mediapipe.tasks.vision.core.RunningMode

class HandsDetector(
    context: Context,
    private val signClassifier: SignClassifier,
    private val onGestureDetected: (String) -> Unit
) {
    private val handLandmarker: HandLandmarker
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath("hand_landmarker.task")
            .build()

        val options = HandLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setNumHands(2)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setResultListener { result, _ ->
                val handLandmarks = result.landmarks()
                if (!handLandmarks.isNullOrEmpty()) {
                    val keypoints = extractKeypoints(handLandmarks[0])
                    val prediction = signClassifier.predict(keypoints)
                    Log.i("HandsDetector", "Gesture detected: $prediction")

                    // update UI di main thread
                    mainHandler.post {
                        onGestureDetected(prediction)
                    }
                }
            }
            .build()

        handLandmarker = HandLandmarker.createFromOptions(context, options)
        Log.d("HandsDetector", "HandLandmarker initialized")
    }

    private fun bitmapToMPImage(bitmap: Bitmap) = BitmapImageBuilder(bitmap).build()

    fun detect(bitmap: Bitmap) {
        val mpImage = bitmapToMPImage(bitmap)
        handLandmarker.detectAsync(mpImage, System.currentTimeMillis())
    }

    fun close() {
        handLandmarker.close()
        Log.d("HandsDetector", "HandLandmarker closed")
    }

    private fun extractKeypoints(landmarks: List<NormalizedLandmark?>?): FloatArray {
        val maxLen = 126
        val keypoints = FloatArray(maxLen)

        if (!landmarks.isNullOrEmpty()) {
            landmarks.forEachIndexed { i, lm ->
                if (lm != null && i * 3 + 2 < maxLen) {
                    keypoints[i * 3] = lm.x()
                    keypoints[i * 3 + 1] = lm.y()
                    keypoints[i * 3 + 2] = lm.z()
                }
            }
        }

        return keypoints
    }
}
