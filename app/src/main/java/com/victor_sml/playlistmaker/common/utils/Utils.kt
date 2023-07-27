package com.victor_sml.playlistmaker.common.utils

import android.content.Context
import android.util.TypedValue
import java.io.File
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

    fun File.toUnique(): File {
        val fileRegex = Regex("(.*?)(\\.[^.]*)?")
        var file = this
        var filename = file.name
        val filePath = file.parent
        var count = 0

        if (file.exists()) {
            if (fileRegex.matches(filename)) {
                val (name, extension) = fileRegex.matchEntire(filename)!!.destructured
                do {
                    count++
                    filename = "$name($count)$extension"
                    file = File(filePath, filename)
                } while (file.exists())
            }
        }
        return file
    }

    fun caseOfTracks(number: Int): String {
        var number = number

        if (number in 11..20)  return "треков"

        if (number > 9) number %= 10

        return when (number % 10) {
            1 -> "трек"
            2, 3, 4 -> "трека"
            0, 5, 6, 7, 8, 9 -> "треков"
            else -> ""
        }
    }
}