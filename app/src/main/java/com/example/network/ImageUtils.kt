package com.example.network

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

fun Bitmap.toBase64(): String {
    val outputStream = ByteArrayOutputStream()
    // Scale down image to avoid hitting payload limits
    val scaledBitmap = Bitmap.createScaledBitmap(this, 1024, (1024.0 * height / width).toInt(), true)
    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
    return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
}
