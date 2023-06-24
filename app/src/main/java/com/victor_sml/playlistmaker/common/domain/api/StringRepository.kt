package com.victor_sml.playlistmaker.common.domain.api

interface StringRepository {
    fun getString(id: String): String
}