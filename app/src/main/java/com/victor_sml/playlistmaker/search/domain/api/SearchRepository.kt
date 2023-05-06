package com.victor_sml.playlistmaker.search.domain.api

import com.victor_sml.playlistmaker.common.models.Track
import com.victor_sml.playlistmaker.search.domain.Resource

interface SearchRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
}