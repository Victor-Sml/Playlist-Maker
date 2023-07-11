package com.victor_sml.playlistmaker.common.domain

import com.victor_sml.playlistmaker.common.domain.api.LibraryInteractor
import com.victor_sml.playlistmaker.common.domain.api.LibraryRepository
import com.victor_sml.playlistmaker.common.models.Track
import kotlinx.coroutines.flow.Flow

class LibraryInteractorImpl(private val libraryRepository: LibraryRepository) : LibraryInteractor {
    override suspend fun addTrackToFavorites(track: Track) {
        libraryRepository.addTrackToFavorites(track)
    }

    override suspend fun deleteTrackFromFavorites(trackId: Int) {
        libraryRepository.deleteTrackFromFavorites(trackId)
    }

    override suspend fun getFavoriteTracks(): Flow<List<Track>> =
        libraryRepository.getFavoriteTracks()
}