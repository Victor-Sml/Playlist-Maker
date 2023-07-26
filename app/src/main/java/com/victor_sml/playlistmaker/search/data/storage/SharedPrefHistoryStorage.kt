package com.victor_sml.playlistmaker.search.data.storage

import android.content.SharedPreferences
import com.google.gson.Gson
import com.victor_sml.playlistmaker.search.data.api.StorageClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SharedPrefHistoryStorage(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : StorageClient {

    override suspend fun putTrackIds(trackIds: IntArray) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit()
                .putString(LOOKED_TRACKS, gson.toJson(trackIds))
                .apply()
        }
    }

    override suspend fun getTrackIds(): IntArray? {
        return withContext(Dispatchers.IO) {
            gson.fromJson(
                sharedPreferences.getString(LOOKED_TRACKS, null),
                IntArray::class.java
            )
        }
    }

    override suspend fun removeTrackIds() {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().remove(LOOKED_TRACKS).apply()
        }
    }

    companion object {
        private const val LOOKED_TRACKS = "looked tracks"
    }
}