package com.victor_sml.playlistmaker.sharing.data.source

import android.content.Context
import com.victor_sml.playlistmaker.sharing.data.SharingRepositoryImpl.StringId
import com.victor_sml.playlistmaker.sharing.data.api.StringSource

class StringSourceImpl(private val context: Context) : StringSource {
    override fun getString(stringId: StringId): String =
        context.getString(getStringIdentifier(stringId.value))

    override fun getStrings(stringIds: Array<StringId>): Array<String> {
        val strings = Array(stringIds.size) { "" }
        for ((index, stringId) in stringIds.withIndex()) {
            strings[index] = context.getString(getStringIdentifier(stringId.value))
        }
        return strings
    }

    private fun getStringIdentifier(stringId: String): Int =
        context.resources.getIdentifier(stringId, "string", context.packageName)
}