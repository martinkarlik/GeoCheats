package com.example.geocheats

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
//    val buffer = planes[0].buffer
//    buffer.rewind()
//    val bytes = ByteArray(buffer.capacity())
//    buffer.get(bytes)

    val planeProxy: ImageProxy.PlaneProxy = this.planes[0]
    val buffer = planeProxy.buffer
    val bytes = ByteArray(buffer.remaining())
    buffer[bytes]


    val orig_bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    return Bitmap.createScaledBitmap(orig_bitmap, 321, 321, false)

}