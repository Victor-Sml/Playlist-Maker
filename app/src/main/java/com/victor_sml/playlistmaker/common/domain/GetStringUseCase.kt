package com.victor_sml.playlistmaker.common.domain

import com.victor_sml.playlistmaker.common.domain.api.StringRepository

class GetStringUseCase(private val repository: StringRepository) {
    fun execute(id: String): String = repository.getString(id)
}