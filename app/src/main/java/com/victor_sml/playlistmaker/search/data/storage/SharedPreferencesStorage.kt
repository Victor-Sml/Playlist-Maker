package com.victor_sml.playlistmaker.search.data.storage

import android.content.SharedPreferences
import com.google.gson.Gson
import com.victor_sml.playlistmaker.common.models.TrackUi
import com.victor_sml.playlistmaker.search.data.api.StorageClient

class SharedPreferencesStorage(private val sharedPreferences: SharedPreferences) : StorageClient {
    override fun putTracks(tracks: List<TrackUi>) {
        sharedPreferences.edit()
            .putString(LOOKED_TRACKS, Gson().toJson(tracks))
            .apply()
    }

    override fun getTracks(): Array<TrackUi>? =
        Gson().fromJson(
            sharedPreferences.getString(LOOKED_TRACKS, null),
            Array<TrackUi>::class.java
        )

    override fun removeTracks() {
        sharedPreferences.edit().remove(LOOKED_TRACKS).apply()
    }

    companion object {
        private const val LOOKED_TRACKS = "looked tracks"
    }
}