package com.victor_sml.playlistmaker.search.domain.api

import com.victor_sml.playlistmaker.common.domain.models.Track
import com.victor_sml.playlistmaker.common.utils.Resource
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchTracks(expression: String): Flow<Resource<List<Track>>>
    fun lookupTracks(trackIds: IntArray): Flow<Resource<List<Track>>>
}