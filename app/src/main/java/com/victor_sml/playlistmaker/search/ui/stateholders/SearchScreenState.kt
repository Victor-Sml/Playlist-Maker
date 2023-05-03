package com.victor_sml.playlistmaker.search.ui.stateholders

import com.victor_sml.playlistmaker.common.models.TrackUi

sealed class SearchScreenState {
    object Loading : SearchScreenState()

    data class SearchResult(val tracks: List<TrackUi>) : SearchScreenState()

    data class History(val tracks: List<TrackUi>) : SearchScreenState()

    object NothingFound : SearchScreenState()

    object ConnectionFailure : SearchScreenState()

    object Empty : SearchScreenState()
}

