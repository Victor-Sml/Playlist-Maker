package com.victor_sml.playlistmaker.common.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.victor_sml.playlistmaker.common.data.db.dto.PlaylistDto
import com.victor_sml.playlistmaker.common.data.db.dto.PlaylistWithTracksDto
import com.victor_sml.playlistmaker.common.data.db.entity.PlaylistEntity
import com.victor_sml.playlistmaker.common.data.db.entity.PlaylistTrackCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlist_track WHERE playlist_id = :playlistId AND track_id = :trackId")
    suspend fun deleteTrack(playlistId: Int, trackId: Int)

    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun insertPlaylistTrackCrossRef(playlistTrackCrossRef: PlaylistTrackCrossRef) :Long

    @Transaction
    @Query("SELECT * FROM playlists WHERE playlist_id = :playlistId")
    fun loadPlaylist(playlistId: Int): Flow<PlaylistWithTracksDto>

    @Query(
        "SELECT p.playlist_id, p.title, p.coverPath, p.description, COUNT(pt.playlist_id) AS numberOfTracks " +
                "FROM playlists AS p LEFT JOIN playlist_track AS pt " +
                "ON pt.playlist_id = p.playlist_id GROUP BY p.playlist_id"
    )
    fun loadPlaylists(): Flow<List<PlaylistDto>>

    @Query("DELETE FROM playlists WHERE playlist_id = :playlistId")
    suspend fun deletePlaylist(playlistId: Int)
}