// SignClassifier.kt
package com.example.signlink.data.signclassifier

import android.content.Context
import android.util.Log
import org.tensorflow.lite.InterpreterApi
import org.tensorflow.lite.InterpreterApi.Options
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

@Suppress("SameParameterValue")
class SignClassifier(context: Context) {

    private val interpreter: InterpreterApi
    private val labels: List<String>

    init {
        val modelFile = loadModelFile(context, "sign_classifier.tflite")
        val options = Options()
        interpreter = InterpreterApi.create(modelFile, options)

        labels = context.assets.open("labels.txt").bufferedReader().readLines()
        Log.d("SignClassifier", "Model loaded: sign_classifier.tflite, size=${modelFile.limit()} bytes")
    }

    private fun loadModelFile(context: Context, filename: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(filename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val channel = inputStream.channel
        return channel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
    }

    fun predict(inputArray: FloatArray): String {
        val input = arrayOf(inputArray)
        val output = Array(1) { FloatArray(labels.size) }
        interpreter.run(input, output)
        val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
        val confidence = if (maxIndex >= 0) output[0][maxIndex] else 0f
        val label = if (maxIndex >= 0) labels[maxIndex] else "Unknown"
        Log.d("SignClassifier", "Predicted gesture: $label (confidence=$confidence)")
        return label
    }
}
