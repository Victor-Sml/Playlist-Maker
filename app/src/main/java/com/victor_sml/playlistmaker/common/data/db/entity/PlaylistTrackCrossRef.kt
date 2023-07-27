package com.victor_sml.playlistmaker.common.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "playlist_tracks",
    primaryKeys = ["playlist_id", "track_id"],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["playlist_id"],
            childColumns = ["playlist_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class PlaylistTrackCrossRef(
    @ColumnInfo(name = "playlist_id")
    val playlistId: Int,
    @ColumnInfo(name = "track_id")
    val trackId: Int,
)