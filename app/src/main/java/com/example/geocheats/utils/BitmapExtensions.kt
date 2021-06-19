package com.example.geocheats.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import timber.log.Timber
import java.nio.ByteBuffer


fun Bitmap.resize(dstWidth: Int, dstHeight: Int) : Bitmap {
    val bitmapRgb: Bitmap = this.copy(Bitmap.Config.ARGB_8888, true)
    return Bitmap.createScaledBitmap(bitmapRgb, dstWidth, dstHeight, false)
}

fun Bitmap.copyBGRToBuffer() : ByteBuffer {

    val buffer: ByteBuffer = ByteBuffer.allocate(width * height * 12)
    buffer.rewind()

    for (y in 0 until height)
        for (x in 0 until width) {

            val index = (y * width + x) * 12
            val color: Int = getPixel(x, y)

            buffer.putFloat(index, Color.blue(color) / 255.0f)
            buffer.putFloat(index + 4, Color.green(color) / 255.0f)
            buffer.putFloat(index + 8, Color.red(color) / 255.0f)

        }

    return buffer


}