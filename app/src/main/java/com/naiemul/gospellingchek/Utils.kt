package com.naiemul.gospellingchek

import android.graphics.Bitmap
import java.util.regex.Pattern
import androidx.core.graphics.scale

object Utils {

    // ===============================
    fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        return bitmap.scale(maxWidth, maxHeight)
    }

    // ===================================
    fun isValidNidFormat(text: String): Boolean {
        // ১০, ১৩ বা ১৭ ডিজিটের এনআইডি ফরম্যাট চেক
        val pattern = Pattern.compile("\\b(\\d{10}|\\d{13}|\\d{17})\\b")

        return pattern.matcher(text).find()
    }
}

