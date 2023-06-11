package com.victor_sml.playlistmaker.search.data.api

interface StorageClient {
    fun putTrackIds(trackIds: List<Int>)
    fun getTrackIds(): Array<Int>?
    fun removeTrackIds()
}