package com.victor_sml.playlistmaker.common.stringProvider.domain

import com.victor_sml.playlistmaker.common.stringProvider.domain.api.StringInteractor
import com.victor_sml.playlistmaker.common.stringProvider.domain.api.StringRepository

class StringInteractorImpl(private val repository: StringRepository) : StringInteractor {
    override fun getString(id: Int): String = repository.getString(id)
}