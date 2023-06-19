package com.victor_sml.playlistmaker.common.stringProvider.data.api

interface StringSource {
    fun getString(id: Int): String
}