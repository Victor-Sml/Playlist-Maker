package com.victor_sml.playlistmaker.search.data

import com.victor_sml.playlistmaker.common.data.db.AppDatabase
import com.victor_sml.playlistmaker.search.data.api.StorageClient
import com.victor_sml.playlistmaker.search.domain.api.HistoryRepository

class HistoryRepositoryImpl(
    private val storage: StorageClient,
    private val db: AppDatabase,
) : HistoryRepository {

    override suspend fun getTracksIds(): IntArray? = storage.getTrackIds()

    override suspend fun putTrackIds(trackIds: IntArray) {
        storage.putTrackIds(trackIds)
    }

    override suspend fun clearHistory() {
        storage.removeTrackIds()
    }

    override suspend fun getFavoriteIds(): IntArray = db.trackDao().getFavoriteTrackIds()
}