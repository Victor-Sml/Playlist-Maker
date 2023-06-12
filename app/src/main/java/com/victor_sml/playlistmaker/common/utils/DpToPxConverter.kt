package com.victor_sml.playlistmaker.common.utils

import android.content.res.Resources

class DpToPxConverter(private val resources: Resources) {
    fun convert(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()
}