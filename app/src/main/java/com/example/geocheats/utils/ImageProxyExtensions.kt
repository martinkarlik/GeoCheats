package com.example.geocheats.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.ImageProxy

fun ImageProxy.toBitmap(): Bitmap {

    val planeProxy: ImageProxy.PlaneProxy = this.planes[0]
    val buffer = planeProxy.buffer
    val bytes = ByteArray(buffer.remaining())
    buffer[bytes]

    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

}