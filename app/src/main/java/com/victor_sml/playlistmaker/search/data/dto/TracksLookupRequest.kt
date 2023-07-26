package com.victor_sml.playlistmaker.search.data.dto

data class TracksLookupRequest( private val trackIds: IntArray) {
    val expression = trackIds.joinToString(",")
}