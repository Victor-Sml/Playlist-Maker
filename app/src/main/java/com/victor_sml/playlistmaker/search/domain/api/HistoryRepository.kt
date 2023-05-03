package com.victor_sml.playlistmaker.search.domain.api

import com.victor_sml.playlistmaker.common.models.TrackUi

interface HistoryRepository {
    fun addTrack(track: TrackUi)
    fun getHistory(): List<TrackUi>?
    fun clearHistory()
}