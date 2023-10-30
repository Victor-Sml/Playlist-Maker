package com.victor_sml.playlistmaker.common.domain.api.tracks

import com.victor_sml.playlistmaker.common.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    suspend fun addTrack(track: Track)
    suspend fun deleteTrackFromFavorites(trackId: Int)
    suspend fun getFavoriteTracks(): Flow<List<Track>>
}