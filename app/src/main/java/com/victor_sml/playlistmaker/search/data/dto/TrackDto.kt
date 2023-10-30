package com.victor_sml.playlistmaker.search.data.dto

import com.victor_sml.playlistmaker.common.domain.models.Track

data class TrackDto(
    val trackId: Int,
    val artistName: String?,
    val trackName: String?,
    val artworkUrl100: String?,
    val trackTimeMillis: Long?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
) {
    fun mapToTrack(isFavorite: Boolean): Track {
        return Track(
            trackId,
            artistName,
            trackName,
            artworkUrl100,
            trackTimeMillis,
            collectionName,
            releaseDate,
            primaryGenreName,
            country,
            previewUrl,
            isFavorite
        )
    }
}