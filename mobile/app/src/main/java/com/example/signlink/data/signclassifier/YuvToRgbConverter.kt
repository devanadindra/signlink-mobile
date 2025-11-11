package com.example.signlink.data.signclassifier

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy

class YuvToRgbConverter {

    fun yuvToRgb(image: ImageProxy, output: Bitmap) {
        val imageY = image.planes[0]
        val imageU = image.planes[1]
        val imageV = image.planes[2]

        val yBuffer = imageY.buffer
        val uBuffer = imageU.buffer
        val vBuffer = imageV.buffer

        val yRowStride = imageY.rowStride
        val uvRowStride = imageU.rowStride
        val uvPixelStride = imageU.pixelStride

        val width = image.width
        val height = image.height

        val pixels = IntArray(width * height)

        val y = ByteArray(yBuffer.remaining())
        val u = ByteArray(uBuffer.remaining())
        val v = ByteArray(vBuffer.remaining())

        yBuffer.get(y)
        uBuffer.get(u)
        vBuffer.get(v)

        var yp = 0
        for (j in 0 until height) {
            val pY = yRowStride * j
            val uvRow = uvRowStride * (j shr 1)
            for (i in 0 until width) {
                val uvOffset = uvRow + (i shr 1) * uvPixelStride
                val yVal = (y[pY + i].toInt() and 0xff)
                val uVal = (u[uvOffset].toInt() and 0xff) - 128
                val vVal = (v[uvOffset].toInt() and 0xff) - 128

                val r = (yVal + 1.370705f * vVal).toInt().coerceIn(0, 255)
                val g = (yVal - 0.337633f * uVal - 0.698001f * vVal).toInt().coerceIn(0, 255)
                val b = (yVal + 1.732446f * uVal).toInt().coerceIn(0, 255)

                pixels[yp++] = -0x1000000 or (r shl 16) or (g shl 8) or b
            }
        }

        output.setPixels(pixels, 0, width, 0, 0, width, height)

    }
}
