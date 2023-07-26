package com.victor_sml.playlistmaker.search.domain

import com.victor_sml.playlistmaker.common.models.Track
import com.victor_sml.playlistmaker.search.domain.api.HistoryInteractor
import com.victor_sml.playlistmaker.search.domain.api.HistoryRepository
import com.victor_sml.playlistmaker.search.domain.api.SearchRepository
import com.victor_sml.playlistmaker.common.utils.Resource
import com.victor_sml.playlistmaker.common.utils.Resource.ErrorState.NOTHING_FOUND

class HistoryInteractorImpl(
    private val historyRepository: HistoryRepository,
    private val searchRepository: SearchRepository,
) : HistoryInteractor {
    private var isRestored = false

    override suspend fun addTrack(track: Track) {
        updateHistory(track)
        historyRepository.putTrackIds(getLookedTracksIds())
    }

    override suspend fun getHistory(): Resource<List<Track>> {
        if (!isRestored) return restoreHistory()
        if (lookedTracks.isNotEmpty()) return Resource.Success(verifyTrackFavorites(lookedTracks))
        return Resource.Error(NOTHING_FOUND)
    }

    private suspend fun verifyTrackFavorites(tracks: List<Track>): List<Track> {
        val tracks = tracks
        val favoriteIds = historyRepository.getFavoriteIds()
        tracks.forEach { it.isFavorite = favoriteIds.contains(it.trackId) }
        return tracks
    }

    override suspend fun clearHistory() {
        lookedTracks.clear()
        historyRepository.clearHistory()
    }

    override suspend fun restoreHistory(): Resource<List<Track>> {
        var result: Resource<List<Track>> = Resource.Error(NOTHING_FOUND)

        val updateLookedTracks: (List<Track>?) -> Unit = { tracks ->
            tracks?.let { lookedTracks.addAll(it) }
            isRestored = true
        }

        historyRepository.getTracksIds()?.let { trackIds ->
            searchRepository.lookupTracks(trackIds).collect { resource ->
                result = resource
                updateLookedTracks(resource.data)
            }
        } ?: updateLookedTracks(null)

        return result
    }

    private fun updateHistory(track: Track) {
        if (lookedTracks.isNotEmpty()) {
            if (track != lookedTracks[0]) lookedTracks.remove(track) else return
        }
        if (lookedTracks.size == HISTORY_SIZE) lookedTracks.removeLast()
        lookedTracks.add(0, track)
    }

    private fun getLookedTracksIds(): IntArray =
        lookedTracks.map { it.trackId }.toIntArray()

    companion object {
        private const val HISTORY_SIZE = 10
        private val lookedTracks = arrayListOf<Track>()
    }
}