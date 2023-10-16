package com.victor_sml.playlistmaker.common.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
class TrackEntity (
    @PrimaryKey
    @ColumnInfo(name="track_id")
    val id: Int,
    @ColumnInfo(name="timestamp")
    val timestamp: Long,
    @ColumnInfo(name="artist_name")
    val artistName: String?,
    @ColumnInfo(name="track_name")
    val trackName: String?,
    @ColumnInfo(name="artwork_url")
    val artworkUrl100: String?,
    @ColumnInfo(name="track_time_millis")
    val trackTimeMillis: Long?,
    @ColumnInfo(name="collection_name")
    val collectionName: String?,
    @ColumnInfo(name="release_date")
    val releaseDate: String?,
    @ColumnInfo(name="genre_name")
    val primaryGenreName: String?,
    @ColumnInfo(name="country")
    val country: String?,
    @ColumnInfo(name="preview_url")
    val previewUrl: String?,
    @ColumnInfo(name="is_favorite")
    val isFavorite: Boolean
)