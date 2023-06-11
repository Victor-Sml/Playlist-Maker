package com.victor_sml.playlistmaker.search.data.storage

import android.content.SharedPreferences
import com.google.gson.Gson
import com.victor_sml.playlistmaker.search.data.api.StorageClient

class SharedPrefHistoryStorage(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : StorageClient {

    override fun putTrackIds(trackIds: List<Int>) {
        sharedPreferences.edit()
            .putString(LOOKED_TRACKS, gson.toJson(trackIds))
            .apply()
    }

    override fun getTrackIds(): Array<Int>? =
        gson.fromJson(
            sharedPreferences.getString(LOOKED_TRACKS, null),
            Array<Int>::class.java
        )

    override fun removeTrackIds() {
        sharedPreferences.edit().remove(LOOKED_TRACKS).apply()
    }

    companion object {
        private const val LOOKED_TRACKS = "looked tracks"
    }
}