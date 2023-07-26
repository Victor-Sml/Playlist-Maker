package com.victor_sml.playlistmaker.common.domain.api

import com.victor_sml.playlistmaker.common.models.Track
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
    suspend fun addTrackToFavorites(track: Track)
    suspend fun deleteTrackFromFavorites(trackId: Int)
    suspend fun getFavoriteTracks(): Flow<List<Track>>
}