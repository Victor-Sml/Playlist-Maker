package com.victor_sml.playlistmaker.search.data.api

interface StorageClient {
    suspend fun putTrackIds(trackIds: List<Int>)
    suspend fun getTrackIds(): Array<Int>?
    suspend fun removeTrackIds()
}