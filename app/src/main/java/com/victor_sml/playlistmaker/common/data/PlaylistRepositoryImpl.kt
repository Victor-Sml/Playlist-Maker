package com.victor_sml.playlistmaker.common.data

import android.database.sqlite.SQLiteException
import com.victor_sml.playlistmaker.common.data.db.convertors.PlaylistConvertor.toPlaylist
import com.victor_sml.playlistmaker.common.data.db.convertors.PlaylistConvertor.toPlaylistEntity
import com.victor_sml.playlistmaker.common.data.db.dao.PlaylistDao
import com.victor_sml.playlistmaker.common.data.db.entity.PlaylistTrackCrossRef
import com.victor_sml.playlistmaker.common.domain.models.Playlist
import com.victor_sml.playlistmaker.common.utils.DBQueryState
import com.victor_sml.playlistmaker.common.utils.DBQueryState.Error
import com.victor_sml.playlistmaker.common.domain.api.playlists.PlaylistRepository
import com.victor_sml.playlistmaker.common.domain.models.PlaylistWithTracks
import com.victor_sml.playlistmaker.common.utils.DBQueryState.Added
import com.victor_sml.playlistmaker.common.utils.DBQueryState.ErrorUnique
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(private val db: PlaylistDao) : PlaylistRepository {

    override suspend fun addPlaylist(playlist: Playlist): DBQueryState {
        try {
            db.insertPlaylist(playlist.toPlaylistEntity())
            return Added
        } catch (e: SQLiteException) {
            return processException(e)
        }
    }

    override suspend fun insertToPlaylist(playlistId: Int, trackId: Int): DBQueryState {
        try {
            db.insertPlaylistTrackCrossRef(PlaylistTrackCrossRef(playlistId, trackId))
            return Added
        } catch (e: SQLiteException) { return processException(e) }
    }

    override suspend fun deleteFromPlaylist(playlistId: Int, trackId: Int) {
        db.deleteTrack(playlistId, trackId)
    }

    override suspend fun loadPlaylist(playlistId: Int): Flow<PlaylistWithTracks> {
        return db.loadPlaylist(playlistId).filterNotNull().map { playlistWithTracks ->
            playlistWithTracks.toPlaylist()
        }
    }

    override suspend fun loadPlaylists(): Flow<List<Playlist>> {
        return db.loadPlaylists().map { playlistsDto ->
            playlistsDto.map { it.toPlaylist() }
        }
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        db.deletePlaylist(playlistId)
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