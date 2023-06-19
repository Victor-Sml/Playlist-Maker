package com.victor_sml.playlistmaker.search.domain

import com.victor_sml.playlistmaker.common.models.Track
import com.victor_sml.playlistmaker.search.domain.api.HistoryInteractor
import com.victor_sml.playlistmaker.search.domain.api.HistoryInteractor.HistoryConsumer
import com.victor_sml.playlistmaker.search.domain.api.HistoryRepository
import com.victor_sml.playlistmaker.search.domain.api.SearchRepository
import com.victor_sml.playlistmaker.common.utils.Resource
import com.victor_sml.playlistmaker.common.utils.Resource.ResponseState.CONNECTION_FAILURE
import com.victor_sml.playlistmaker.common.utils.Resource.ResponseState.NOTHING_FOUND
import com.victor_sml.playlistmaker.common.utils.Resource.ResponseState.SUCCESS
import java.util.concurrent.ExecutorService

class HistoryInteractorImpl(
    private val historyRepository: HistoryRepository,
    private val searchRepository: SearchRepository,
    private val executor: ExecutorService
) : HistoryInteractor {
    private var isRestored = false

    override fun restoreHistory(consumer: HistoryConsumer) {
        executor.execute {
            synchronized(this) {
                val trackIds = historyRepository.getTracksIds()
                val resource = trackIds?.let { searchRepository.lookupTracks(it) } ?:
                Resource.Error(NOTHING_FOUND)

                consumer.consume(resource.data, resource.responseState)
                if (resource.responseState == CONNECTION_FAILURE) return@execute
                resource.data?.let { lookedTracks.addAll(it) }
                isRestored = true
            }
        }
    }

    override fun getHistory(consumer: HistoryConsumer) {
        executor.execute {
            if (!isRestored) {
                restoreHistory(consumer)
                return@execute
            }
            synchronized(this) {
                if (lookedTracks.isNotEmpty()) {
                    consumer.consume(lookedTracks, SUCCESS)
                } else consumer.consume(requestState = NOTHING_FOUND)
            }
        }
    }

    private fun updateHistory(track: Track) {
        synchronized(this) {
            if (lookedTracks.isNotEmpty()) {
                if (track != lookedTracks[0]) lookedTracks.remove(track) else return
            }
            if (lookedTracks.size == HISTORY_SIZE) lookedTracks.removeLast()
            lookedTracks.add(0, track)
        }
    }

    override fun clearHistory() {
        synchronized(this) { lookedTracks.clear() }
        executor.execute { historyRepository.clearHistory() }
    }

    override fun addTrack(track: Track) {
        executor.execute {
            synchronized(this) {
                updateHistory(track)
                historyRepository.putTrackIds(getLookedTracksIds())
            }
        }
    }

    private fun getLookedTracksIds(): ArrayList<Int> =
        lookedTracks.map { it.trackId }.toCollection(arrayListOf())

    companion object {
        private const val HISTORY_SIZE = 10
        private val lookedTracks = arrayListOf<Track>()
    }
}