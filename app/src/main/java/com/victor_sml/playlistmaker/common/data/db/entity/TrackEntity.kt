package com.victor_sml.playlistmaker.common.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
class TrackEntity (
    @PrimaryKey
    val id: Int,
    val timestamp: Long,
    val artistName: String?,
    val trackName: String?,
    val artworkUrl100: String?,
    val trackTimeMillis: Long?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?,
    val isFavorite: Boolean
)