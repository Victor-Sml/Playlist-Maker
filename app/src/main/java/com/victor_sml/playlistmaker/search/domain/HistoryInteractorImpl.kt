package com.victor_sml.playlistmaker.search.domain

import com.victor_sml.playlistmaker.common.models.TrackUi
import com.victor_sml.playlistmaker.search.domain.api.HistoryInteractor
import com.victor_sml.playlistmaker.search.domain.api.HistoryInteractor.HistoryConsumer
import com.victor_sml.playlistmaker.search.domain.api.HistoryRepository
import java.util.concurrent.ExecutorService

class HistoryInteractorImpl(
    private val repository: HistoryRepository,
    private val executor: ExecutorService
) : HistoryInteractor {

    override fun addTrack(track: TrackUi) {
        executor.execute { repository.addTrack(track) }
    }

    override fun getHistory(consumer: HistoryConsumer) {
        executor.execute { consumer.consume(repository.getHistory()) }
    }

    override fun clearHistory() {
        executor.execute { repository.clearHistory() }
    }
}