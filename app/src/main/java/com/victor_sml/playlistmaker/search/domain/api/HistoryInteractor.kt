package com.victor_sml.playlistmaker.search.domain.api

import com.victor_sml.playlistmaker.common.domain.models.Track
import com.victor_sml.playlistmaker.common.utils.Resource

interface HistoryInteractor {
    suspend fun restoreHistory(): Resource<List<Track>>
    suspend fun addTrack(track: Track)
    suspend fun getHistory(): Resource<List<Track>>
    suspend fun clearHistory()
}