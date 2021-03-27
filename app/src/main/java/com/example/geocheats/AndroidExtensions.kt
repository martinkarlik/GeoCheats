package com.example.geocheats

import android.R.attr.bitmap
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.camera.core.ImageProxy
import timber.log.Timber
import java.lang.Math.round
import java.nio.ByteBuffer
import kotlin.math.roundToInt


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

fun Bitmap.copyRGBToBuffer() : ByteBuffer {

    val buffer: ByteBuffer = ByteBuffer.allocate(width * height * 12)
    buffer.rewind()

    for (y in 0 until height)
        for (x in 0 until width) {

            val index = (y * width + x) * 12
            val color: Int = getPixel(x, y)

            buffer.putFloat(index, Color.red(color) / 255.0f)
            buffer.putFloat(index + 4, Color.green(color) / 255.0f)
            buffer.putFloat(index + 8, Color.blue(color) / 255.0f)

//            if (y < 5 && x < 5) {
//                Timber.i("Actually: %d %d %d".format(Color.red(color), Color.green(color), Color.blue(color)))
//                Timber.i("In buffer: %f %f %f".format(buffer.getFloat(index), buffer.getFloat(index + 4), buffer.getFloat(index + 8)))
//            }

    }

    return buffer


}

