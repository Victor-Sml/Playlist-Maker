package com.victor_sml.playlistmaker.search.data

import com.victor_sml.playlistmaker.search.data.api.StorageClient
import com.victor_sml.playlistmaker.search.domain.api.HistoryRepository

class HistoryRepositoryImpl(
    private val storage: StorageClient
) : HistoryRepository {

    override suspend fun getTracksIds(): Array<Int>? = storage.getTrackIds()

    override suspend fun putTrackIds(trackIds: ArrayList<Int>) {
        storage.putTrackIds(trackIds)
    }

    override suspend fun clearHistory() {
        storage.removeTrackIds()
    }
}