package com.victor_sml.playlistmaker.common.data

import com.victor_sml.playlistmaker.common.data.api.StringSource
import com.victor_sml.playlistmaker.common.domain.api.StringRepository

class StringRepositoryImpl(private val stringProvider: StringSource) : StringRepository {
    override fun getString(id: String): String = stringProvider.getString(id)
}