package com.victor_sml.playlistmaker.common.data

import android.content.Context
import com.victor_sml.playlistmaker.common.data.api.StringSource

class StringSourceImpl(private val context: Context) : StringSource {
    override fun getString(id: String): String =
        context.getString(getStringIdentifier(id))

    private fun getStringIdentifier(id: String): Int =
        context.resources.getIdentifier(id, "string", context.packageName)
}