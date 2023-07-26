package com.victor_sml.playlistmaker.common.data

import com.victor_sml.playlistmaker.common.data.db.AppDatabase
import com.victor_sml.playlistmaker.common.data.db.convertors.TrackConvertor.toTrack
import com.victor_sml.playlistmaker.common.data.db.convertors.TrackConvertor.toTrackEntity
import com.victor_sml.playlistmaker.common.domain.api.LibraryRepository
import com.victor_sml.playlistmaker.common.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LibraryRepositoryImpl(private val db: AppDatabase) : LibraryRepository {
    override suspend fun addTrackToFavorites(track: Track) {
        db.trackDao().insertTrack(track.toTrackEntity())
    }

    override suspend fun deleteTrackFromFavorites(trackId: Int) {
        db.trackDao().deleteTrack(trackId)
    }

    override suspend fun getFavoriteTracks(): Flow<List<Track>> = flow {
        emit(db.trackDao().getFavoriteTracks().map { it.toTrack() })
    }
}