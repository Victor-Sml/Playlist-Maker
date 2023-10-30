package com.victor_sml.playlistmaker.common.utils

import android.content.Context
import android.content.res.Resources
import com.victor_sml.playlistmaker.common.Constants.CASE_OF_TRACK_PLURALS_ID
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

object Utils {
    fun Number.toTimeMMSS(): String = SimpleDateFormat("mm:ss", Locale.getDefault()).format(this)

    fun String.toDateYYYY(): String = this.take(4)

    fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    fun Int.toNumberOfTracksString(context: Context) =
        context.resources.getQuantityString(
            CASE_OF_TRACK_PLURALS_ID,
            this,
            this
        )

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
}