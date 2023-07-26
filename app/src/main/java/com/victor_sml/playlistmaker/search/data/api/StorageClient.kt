package com.victor_sml.playlistmaker.search.data.api

interface StorageClient {
    suspend fun putTrackIds(trackIds: IntArray)
    suspend fun getTrackIds(): IntArray?
    suspend fun removeTrackIds()
}