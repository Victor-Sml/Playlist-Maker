package com.victor_sml.playlistmaker.utils

import android.content.Context
import android.util.TypedValue

/**
 * Конвертирует dp в px
 */
fun dpToPx(dp: Int, context: Context): Int {
    val resources = context.resources
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources.displayMetrics
    ).toInt()
}