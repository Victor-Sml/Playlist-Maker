package com.victor_sml.playlistmaker.sharing.data.api

import com.victor_sml.playlistmaker.sharing.data.SharingRepositoryImpl.StringId

interface StringSource {
    fun getString(stringId: StringId): String
    fun getStrings(stringIds: Array<StringId>): Array<String>
}