// ui/utils/ImageUtils.kt
package com.speechify.composeuichallenge.ui.utils

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

fun createPlaceholderBitmap(width: Int = 100, height: Int = 100): ImageBitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.eraseColor(Color.parseColor("#CCCCCC"))
    return bitmap.asImageBitmap()
}