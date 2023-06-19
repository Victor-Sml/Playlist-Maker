package com.victor_sml.playlistmaker.search.domain.api

import com.victor_sml.playlistmaker.common.models.Track
import com.victor_sml.playlistmaker.common.utils.Resource.ResponseState

interface HistoryInteractor {
    fun restoreHistory(consumer: HistoryConsumer)
    fun addTrack(track: Track)
    fun getHistory(consumer: HistoryConsumer)
    fun clearHistory()

    interface HistoryConsumer {
        fun consume(tracks: List<Track>? = null, requestState: ResponseState)
    }
}