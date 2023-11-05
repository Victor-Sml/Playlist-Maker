package com.victor_sml.playlistmaker.common.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.victor_sml.playlistmaker.common.data.db.dto.PlaylistDto
import com.victor_sml.playlistmaker.common.data.db.dto.PlaylistWithTracksDto
import com.victor_sml.playlistmaker.common.data.db.entity.PlaylistEntity
import com.victor_sml.playlistmaker.common.data.db.entity.PlaylistTrackCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Transaction
    @Query("SELECT * FROM playlists WHERE playlist_id = :playlistId")
    fun loadPlaylist(playlistId: Int): Flow<PlaylistWithTracksDto>

    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun insertPlaylistTrackCrossRef(playlistTrackCrossRef: PlaylistTrackCrossRef) :Long

    @Query(
        "SELECT p.playlist_id, p.title, p.coverPath, p.description, COUNT(pt.playlist_id) AS numberOfTracks " +
                "FROM playlists AS p LEFT JOIN playlist_track AS pt " +
                "ON pt.playlist_id = p.playlist_id GROUP BY p.playlist_id"
    )
    fun loadPlaylists(): Flow<List<PlaylistDto>>
}