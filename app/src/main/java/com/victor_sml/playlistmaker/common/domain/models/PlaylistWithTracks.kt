package com.victor_sml.playlistmaker.common.domain.models

data class PlaylistWithTracks(
    val id: Int,
    val title: String,
    val coverPath: String?,
    val description: String?,
    val tracks: List<Track>? = null,
)