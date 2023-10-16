package com.victor_sml.playlistmaker.common.domain

import com.victor_sml.playlistmaker.common.domain.api.TracksInteractor
import com.victor_sml.playlistmaker.common.domain.api.TrackRepository
import com.victor_sml.playlistmaker.common.models.Track
import kotlinx.coroutines.flow.Flow

class TracksInteractorImpl(private val trackRepository: TrackRepository) : TracksInteractor {
    override suspend fun addTrack(track: Track) {
        trackRepository.addTrack(track)
    }

    override suspend fun deleteTrackFromFavorites(trackId: Int) {
        trackRepository.deleteTrackFromFavorites(trackId)
    }

    override suspend fun getFavoriteTracks(): Flow<List<Track>> =
        trackRepository.getFavoriteTracks()
}