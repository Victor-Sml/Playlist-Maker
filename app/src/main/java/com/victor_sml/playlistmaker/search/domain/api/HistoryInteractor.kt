package com.victor_sml.playlistmaker.search.domain.api

import com.victor_sml.playlistmaker.common.models.TrackUi

interface HistoryInteractor {
    fun addTrack(track: TrackUi)
    fun getHistory(consumer: HistoryConsumer)
    fun clearHistory()

    interface HistoryConsumer {
        fun consume(tracks: List<TrackUi>?)
    }
}