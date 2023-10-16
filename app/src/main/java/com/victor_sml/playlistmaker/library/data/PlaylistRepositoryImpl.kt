package com.victor_sml.playlistmaker.library.data

import android.database.sqlite.SQLiteException
import android.util.Log
import com.victor_sml.playlistmaker.common.data.db.AppDatabase
import com.victor_sml.playlistmaker.common.data.db.convertors.PlaylistConvertor.toPlaylist
import com.victor_sml.playlistmaker.common.data.db.convertors.PlaylistConvertor.toPlaylistEntity
import com.victor_sml.playlistmaker.common.data.db.entity.PlaylistTrackCrossRef
import com.victor_sml.playlistmaker.common.models.Playlist
import com.victor_sml.playlistmaker.common.utils.DBQueryState
import com.victor_sml.playlistmaker.common.utils.DBQueryState.Error
import com.victor_sml.playlistmaker.common.utils.DBQueryState.ErrorUnique
import com.victor_sml.playlistmaker.common.utils.DBQueryState.Ok
import com.victor_sml.playlistmaker.library.domain.api.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(private val db: AppDatabase) : PlaylistRepository {
    override suspend fun addPlaylist(playlist: Playlist): DBQueryState {
        try {
            db.playlistDao().insertPlaylist(playlist.toPlaylistEntity())
            return Ok
        } catch (e: SQLiteException) {
            return processException(e)
        }
    }

    override suspend fun loadPlaylists(): Flow<List<Playlist>> =
        db.playlistDao().loadPlaylists().map {
            it.map { PlaylistWithTracks ->
                PlaylistWithTracks.toPlaylist()
            }
        }

    override suspend fun insertToPlaylist(playlistId: Int, trackId: Int): DBQueryState {
        try {
            val playlistTrackCrossRef = PlaylistTrackCrossRef(playlistId, trackId)

            db.playlistDao().insertPlaylistTrackCrossRef(playlistTrackCrossRef)
            return Ok
        } catch (e: SQLiteException) {
            Log.d("MyLogs", e.toString())
            return processException(e)
        }
    }

    private fun processException(e: SQLiteException): DBQueryState {
        e.message?.let { message ->
            if (message.startsWith(SQLITE_CONSTRAINT_UNIQUE)) return ErrorUnique
        }
        return Error()
    }

    companion object {
        private const val SQLITE_CONSTRAINT_UNIQUE = "UNIQUE constraint failed"
    }
}