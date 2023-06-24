package com.victor_sml.playlistmaker.search.domain.api

interface HistoryRepository {
    suspend fun putTrackIds(trackIds: ArrayList<Int>)
    suspend fun clearHistory()
    suspend fun getTracksIds(): Array<Int>?
}