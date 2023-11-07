package com.victor_sml.playlistmaker.common.data

import com.victor_sml.playlistmaker.common.data.db.convertors.TrackConvertor.toTrack
import com.victor_sml.playlistmaker.common.data.db.convertors.TrackConvertor.toTrackEntity
import com.victor_sml.playlistmaker.common.data.db.dao.TrackDao
import com.victor_sml.playlistmaker.common.domain.api.tracks.TrackRepository
import com.victor_sml.playlistmaker.common.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(private val db: TrackDao) : TrackRepository {
    override suspend fun addTrack(track: Track) {
        db.insertTrack(track.toTrackEntity())
    }

    override suspend fun deleteTrackFromFavorites(trackId: Int) {
        with(db) {
            if (isTrackInPlaylist(trackId)) updateFavoriteState(trackId, false)
            else deleteTrack(trackId)
        }
    }

    override suspend fun getFavoriteTracks(): Flow<List<Track>> = flow {
        emit(db.getFavoriteTracks().map { it.toTrack() })
    }

    override suspend fun verifyTrackFavorites(trackId: Int): Boolean {
        return db.getFavoriteTrackIds().contains(trackId)
    }
}