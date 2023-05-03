package com.victor_sml.playlistmaker.search.data

import com.victor_sml.playlistmaker.common.models.TrackUi
import com.victor_sml.playlistmaker.search.data.api.StorageClient
import com.victor_sml.playlistmaker.search.domain.api.HistoryRepository

class HistoryRepositoryImpl(private val storage: StorageClient) : HistoryRepository {
    init {
        if (lookedTracks.isEmpty()) storage.getTracks()?.toCollection(lookedTracks)
    }

    override fun getHistory(): List<TrackUi> = lookedTracks

    override fun addTrack(track: TrackUi) {
        updateHistory(track)
        storage.putTracks(lookedTracks)
    }

    override fun clearHistory() {
        lookedTracks.clear()
        storage.removeTracks()
    }

    private fun updateHistory(track: TrackUi) {
        if (lookedTracks.isNotEmpty()) {
            if (track != lookedTracks[0]) lookedTracks.remove(track) else return
        }

        if (lookedTracks.size == HISTORY_SIZE) lookedTracks.removeLast()
        lookedTracks.add(0, track)
    }

    companion object {
        private const val HISTORY_SIZE = 10
        private val lookedTracks: ArrayList<TrackUi> = arrayListOf()
    }
}