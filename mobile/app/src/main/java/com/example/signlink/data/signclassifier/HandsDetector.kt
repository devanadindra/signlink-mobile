package com.example.signlink.data.signclassifier

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker.HandLandmarkerOptions

@Suppress("UNCHECKED_CAST")
class HandsDetector(context: Context, private val signClassifier: SignClassifier) {

    private val handLandmarker: HandLandmarker

    val baseOptions = BaseOptions.builder()
        .setModelAssetPath("hand_landmarker.task")
        .build()

    init {
        val options = HandLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setNumHands(1)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setResultListener { result, inputImage ->
                // result = HandLandmarkerResult
                val handLandmarks = result.landmarks()
                if (!handLandmarks.isNullOrEmpty()) {
                    val keypoints = extractKeypoints(handLandmarks[0])
                    val prediction = signClassifier.predict(keypoints)
                    Log.d("SignClassifier", "Predicted gesture: $prediction")
                }
            }
            .build()

        handLandmarker = HandLandmarker.createFromOptions(context, options)
    }

    fun bitmapToMPImage(bitmap: Bitmap): MPImage {
        return BitmapImageBuilder(bitmap).build()
    }

    fun detect(bitmap: Bitmap) {
        val mpImage = bitmapToMPImage(bitmap)
        val result = handLandmarker.detect(mpImage)

        val handLandmarks = result.landmarks()
        if (!handLandmarks.isNullOrEmpty()) {
            val keypoints = extractKeypoints(handLandmarks[0])
            val prediction = signClassifier.predict(keypoints)
            Log.d("SignClassifier", "Predicted gesture: $prediction")
        }
    }

    fun close() {
        handLandmarker.close()
        Log.d("HandsDetector", "HandLandmarker resources successfully closed.")
    }
}
