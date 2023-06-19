package com.victor_sml.playlistmaker.search.domain.api

interface HistoryRepository {
    fun putTrackIds(trackIds: ArrayList<Int>)
    fun getTracksIds(): Array<Int>?
    fun clearHistory()
}