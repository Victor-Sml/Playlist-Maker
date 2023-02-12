package com.victor_sml.playlistmaker


data class Track(
    val artistName: String,
    val trackName: String,
    val artworkUrl100: String,
    val trackTimeMillis: Long,
)