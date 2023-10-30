package com.victor_sml.playlistmaker.common.data.db.dto

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.victor_sml.playlistmaker.common.data.db.entity.PlaylistEntity
import com.victor_sml.playlistmaker.common.data.db.entity.PlaylistTrackCrossRef
import com.victor_sml.playlistmaker.common.data.db.entity.TrackEntity

data class PlaylistWithTracksDto(
    @Embedded val playlist: PlaylistEntity,
    @Relation(
        parentColumn = "playlist_id",
        entityColumn = "track_id",
        associateBy = Junction(PlaylistTrackCrossRef::class)
    )
    val tracks: List<TrackEntity>
)