package com.example.geocheats

import android.R.attr.bitmap
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.ImageProxy
import java.nio.ByteBuffer


fun ByteBuffer.toByteArray(): ByteArray {
    rewind()    // Rewind the buffer to zero
    val data = ByteArray(remaining())
    get(data)   // Copy the buffer into a byte array
    return data // Return the byte array
}

fun ImageProxy.toBitmap(): Bitmap {

    val planeProxy: ImageProxy.PlaneProxy = this.planes[0]
    val buffer = planeProxy.buffer
    val bytes = ByteArray(buffer.remaining())
    buffer[bytes]

    val orig_bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    val bitmap_rgb: Bitmap = orig_bitmap.copy(Bitmap.Config.ARGB_8888, true)
    return Bitmap.createScaledBitmap(bitmap_rgb, 299, 299, false)

}

