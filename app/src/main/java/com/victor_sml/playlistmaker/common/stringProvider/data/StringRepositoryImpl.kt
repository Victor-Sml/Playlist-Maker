package com.victor_sml.playlistmaker.common.stringProvider.data

import com.victor_sml.playlistmaker.common.stringProvider.data.api.StringSource
import com.victor_sml.playlistmaker.common.stringProvider.domain.api.StringRepository

class StringRepositoryImpl(private val stringProvider: StringSource) : StringRepository {
    override fun getString(id: Int): String = stringProvider.getString(id)
}