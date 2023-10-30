package com.victor_sml.playlistmaker.common.data.db.convertors

import com.victor_sml.playlistmaker.common.data.db.entity.TrackEntity
import com.victor_sml.playlistmaker.common.domain.models.Track
import java.util.Calendar

object TrackConvertor {

    fun Track.toTrackEntity() = TrackEntity(
        id = trackId,
        timestamp = Calendar.getInstance().timeInMillis,
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

    fun TrackEntity.toTrack() = Track(
        id,
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