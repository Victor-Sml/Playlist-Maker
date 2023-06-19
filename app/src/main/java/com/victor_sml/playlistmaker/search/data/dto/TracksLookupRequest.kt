package com.victor_sml.playlistmaker.search.data.dto

data class TracksLookupRequest( private val trackIds: Array<Int>) {
    val expression = trackIds.joinToString(",")
}