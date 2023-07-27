package com.victor_sml.playlistmaker.common.models

data class Playlist(
    val id: Int,
    val title: String,
    val coverPath: String?,
    val description: String?,
    val tracks: List<Track>? = null
)