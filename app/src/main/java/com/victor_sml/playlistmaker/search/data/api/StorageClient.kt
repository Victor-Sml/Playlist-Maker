package com.victor_sml.playlistmaker.search.data.api

import com.victor_sml.playlistmaker.common.models.TrackUi

interface StorageClient {
    fun putTracks(tracks: List<TrackUi>)
    fun getTracks(): Array<TrackUi>?
    fun removeTracks()
}