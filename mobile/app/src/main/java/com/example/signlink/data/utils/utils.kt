package com.example.signlink.data.utils

import android.content.Context
import android.net.Uri
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object utils {
    fun uriToFile(uri: Uri, context: Context): File {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val file = File.createTempFile("upload_", ".mp4", context.cacheDir)
        val outputStream = FileOutputStream(file)

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

     fun parseErrorMessage(json: String?): String? {
        return try {
            val jsonObj = JSONObject(json ?: return null)
            if (jsonObj.has("errors")) {
                val errors = jsonObj.getJSONArray("errors")
                errors.join(", ")
            } else {
                jsonObj.toString()
            }
        } catch (e: Exception) {
            e.message
        }
    }

}