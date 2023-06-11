package com.victor_sml.playlistmaker.common.stringProvider.data

import android.content.res.Resources
import com.victor_sml.playlistmaker.common.stringProvider.data.api.StringSource

class StringSourceImpl(private val resources: Resources): StringSource {
    override fun getString(id: Int): String = resources.getString(id)
}