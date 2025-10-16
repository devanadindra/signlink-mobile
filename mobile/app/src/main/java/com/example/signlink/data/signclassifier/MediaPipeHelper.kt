package com.example.signlink.data.signclassifier

fun extractKeypoints(landmarks: List<com.google.mediapipe.tasks.components.containers.NormalizedLandmark?>?): FloatArray {
    if (landmarks.isNullOrEmpty()) return FloatArray(0)

    val keypoints = FloatArray(landmarks.size * 3)

    landmarks.forEachIndexed { i, lm ->
        if (lm != null) {
            keypoints[i * 3] = lm.x()
            keypoints[i * 3 + 1] = lm.y()
            keypoints[i * 3 + 2] = lm.z()
        }
    }

    return keypoints
}