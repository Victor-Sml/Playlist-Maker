package com.victor_sml.playlistmaker.common.domain

import com.victor_sml.playlistmaker.common.domain.api.TrackInteractor
import com.victor_sml.playlistmaker.common.domain.api.TrackRepository
import com.victor_sml.playlistmaker.common.models.Track
import kotlinx.coroutines.flow.Flow

class TrackInteractorImpl(private val trackRepository: TrackRepository) : TrackInteractor {
    override suspend fun addTrackToFavorites(track: Track) {
        trackRepository.addTrackToFavorites(track)
    }

    override suspend fun deleteTrackFromFavorites(trackId: Int) {
        trackRepository.deleteTrackFromFavorites(trackId)
    }

    override suspend fun getFavoriteTracks(): Flow<List<Track>> =
        trackRepository.getFavoriteTracks()
}