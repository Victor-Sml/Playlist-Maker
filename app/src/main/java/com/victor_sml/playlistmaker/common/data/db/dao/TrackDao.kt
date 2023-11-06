package com.victor_sml.playlistmaker.common.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.victor_sml.playlistmaker.common.data.db.entity.TrackEntity

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("DELETE FROM tracks WHERE track_id = :trackId")
    suspend fun deleteTrack(trackId: Int)

    @Query("SELECT track_id FROM tracks WHERE is_favorite = 1")
    suspend fun getFavoriteTrackIds(): IntArray

    @Query("SELECT * FROM tracks WHERE is_favorite = 1 ORDER BY timestamp DESC")
    suspend fun getFavoriteTracks(): List<TrackEntity>

    @Query("UPDATE tracks SET is_favorite = :isFavorite WHERE track_id = :trackId")
    suspend fun updateFavoriteState(trackId: Int, isFavorite: Boolean)

    @Query("SELECT EXISTS(SELECT * FROM playlist_track WHERE track_id = :trackId)")
    suspend fun isTrackInPlaylist(trackId: Int): Boolean
}