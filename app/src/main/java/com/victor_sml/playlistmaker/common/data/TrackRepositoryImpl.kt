package com.victor_sml.playlistmaker.common.data

import com.victor_sml.playlistmaker.common.data.db.AppDatabase
import com.victor_sml.playlistmaker.common.data.db.convertors.TrackConvertor.toTrack
import com.victor_sml.playlistmaker.common.data.db.convertors.TrackConvertor.toTrackEntity
import com.victor_sml.playlistmaker.common.domain.api.tracks.TrackRepository
import com.victor_sml.playlistmaker.common.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(private val db: AppDatabase) : TrackRepository {
    override suspend fun addTrack(track: Track) {
        db.trackDao().insertTrack(track.toTrackEntity())
    }

    override suspend fun deleteTrackFromFavorites(trackId: Int) {
        db.trackDao().deleteTrack(trackId)
    }

    override suspend fun getFavoriteTracks(): Flow<List<Track>> = flow {
        emit(db.trackDao().getFavoriteTracks().map { it.toTrack() })
    }
}