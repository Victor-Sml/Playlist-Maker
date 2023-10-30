package com.victor_sml.playlistmaker.common.data.db.dto

import androidx.room.Embedded
import com.victor_sml.playlistmaker.common.data.db.entity.PlaylistEntity

data class PlaylistDto(
    @Embedded val playlist: PlaylistEntity,
    val numberOfTracks: Int
)