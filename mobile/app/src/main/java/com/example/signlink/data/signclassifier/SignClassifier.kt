package com.example.signlink.data.signclassifier

import android.content.Context
import org.tensorflow.lite.InterpreterApi
import org.tensorflow.lite.InterpreterApi.Options
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SignClassifier(context: Context) {
    private val interpreter: InterpreterApi
    private val labels: List<String>

    init {
        val modelFile = loadModelFile(context, "sign_classifier.tflite")

        val options = Options()
        interpreter = InterpreterApi.create(modelFile, options)

        labels = context.assets.open("labels.txt").bufferedReader().readLines()
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
        return if (maxIndex >= 0) labels[maxIndex] else "Unknown"
    }
}
