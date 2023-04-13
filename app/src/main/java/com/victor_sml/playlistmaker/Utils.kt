package com.victor_sml.playlistmaker

import android.content.Context
import android.util.TypedValue
import java.text.SimpleDateFormat
import java.util.Locale

object Utils {
    fun millisToMMSS(progress: Number): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(progress)

    fun dpToPx(dp: Int, context: Context): Int {
        val resources = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }
}