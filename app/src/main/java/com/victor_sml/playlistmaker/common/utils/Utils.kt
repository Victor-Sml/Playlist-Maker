package com.victor_sml.playlistmaker.common.utils

import android.content.Context
import android.util.TypedValue
import java.text.SimpleDateFormat
import java.util.Locale

object Utils {
    fun Number.toTimeMMSS(): String = SimpleDateFormat("mm:ss", Locale.getDefault()).format(this)

    fun String.toDateYYYY(): String = this.take(4)

    fun dpToPx(dp: Int, context: Context): Int {
        val resources = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }
}
