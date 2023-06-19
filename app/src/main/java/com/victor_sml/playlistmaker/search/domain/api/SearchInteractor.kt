package com.victor_sml.playlistmaker.search.domain.api

import com.victor_sml.playlistmaker.common.models.Track
import com.victor_sml.playlistmaker.common.utils.Resource.ResponseState

interface SearchInteractor {
    fun searchTracks(expression: String, consumer: SearchResultConsumer)

    interface SearchResultConsumer {
        fun consume(tracks: List<Track>?, requestState: ResponseState)
    }
}