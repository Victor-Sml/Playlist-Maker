package com.victor_sml.playlistmaker.search.domain.api

import com.victor_sml.playlistmaker.common.models.Track
import com.victor_sml.playlistmaker.common.utils.Resource

interface SearchRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
    fun lookupTracks(trackIds: Array<Int>): Resource<List<Track>>
}