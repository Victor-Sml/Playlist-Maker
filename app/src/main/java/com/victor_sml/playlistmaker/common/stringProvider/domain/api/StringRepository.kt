package com.victor_sml.playlistmaker.common.stringProvider.domain.api

interface StringRepository {
    fun getString(id: Int): String
}