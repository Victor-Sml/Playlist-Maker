package com.victor_sml.playlistmaker.search.domain.api

interface HistoryRepository {
    suspend fun putTrackIds(trackIds: IntArray)
    suspend fun clearHistory()
    suspend fun getTracksIds(): IntArray?
    suspend fun getFavoriteIds(): IntArray
}