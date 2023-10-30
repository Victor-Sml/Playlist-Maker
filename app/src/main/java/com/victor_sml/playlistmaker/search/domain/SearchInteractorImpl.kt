package com.victor_sml.playlistmaker.search.domain

import com.victor_sml.playlistmaker.common.domain.models.Track
import com.victor_sml.playlistmaker.common.utils.Resource
import com.victor_sml.playlistmaker.search.domain.api.SearchInteractor
import com.victor_sml.playlistmaker.search.domain.api.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchInteractorImpl(
    private val repository: SearchRepository
) : SearchInteractor {
    override fun searchTracks(expression: String): Flow<Pair<List<Track>?, Resource.ResponseState>> {
        return repository.searchTracks(expression)
            .map { result -> Pair(result.data, result.responseState) }
    }
}