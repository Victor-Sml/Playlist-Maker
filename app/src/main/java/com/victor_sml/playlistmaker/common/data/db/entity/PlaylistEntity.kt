package com.victor_sml.playlistmaker.common.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="playlist_id")
    val id: Int,
    @ColumnInfo(name="title")
    val title: String,
    @ColumnInfo(name="coverPath")
    val coverPath: String?,
    @ColumnInfo(name="description")
    val description: String?,
)