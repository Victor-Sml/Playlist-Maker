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

    @Query("DELETE FROM tracks WHERE id = :trackId")
    suspend fun deleteTrack(trackId: Int)

    @Query("SELECT id FROM tracks WHERE isFavorite = 1")
    suspend fun getFavoriteTrackIds(): IntArray

    @Query("SELECT * FROM tracks WHERE isFavorite = 1 ORDER BY timestamp DESC")
    suspend fun getFavoriteTracks(): List<TrackEntity>
}