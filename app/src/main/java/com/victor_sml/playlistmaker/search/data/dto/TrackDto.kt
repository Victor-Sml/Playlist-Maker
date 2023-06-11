package com.victor_sml.playlistmaker.search.data.dto

import com.victor_sml.playlistmaker.common.models.Track
import com.victor_sml.playlistmaker.common.utils.Utils.toDateYYYY
import com.victor_sml.playlistmaker.common.utils.Utils.toTimeMMSS

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
    fun mapToTrack(): Track {
        return Track(
            trackId,
            artistName,
            trackName,
            artworkUrl100,
            trackTimeMillis?.toTimeMMSS(),
            collectionName,
            releaseDate?.toDateYYYY(),
            primaryGenreName,
            country,
            previewUrl
        )
    }
}