package com.victor_sml.playlistmaker.common.domain

import com.victor_sml.playlistmaker.common.domain.models.Playlist
import com.victor_sml.playlistmaker.common.utils.DBQueryState
import com.victor_sml.playlistmaker.common.domain.api.playlists.PlaylistInteractor
import com.victor_sml.playlistmaker.common.domain.api.playlists.PlaylistRepository
import com.victor_sml.playlistmaker.common.domain.models.PlaylistWithTracks
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {

    override suspend fun addPlaylist(playlist: Playlist): DBQueryState =
        playlistRepository.addPlaylist(playlist)

    override suspend fun loadPlaylist(playlistId: Int): Flow<PlaylistWithTracks> =
        playlistRepository.loadPlaylist(playlistId)

    override suspend fun loadPlaylists(): Flow<List<Playlist>?> =
        playlistRepository.loadPlaylists()

    override suspend fun insertToPlaylist(playlistId: Int, trackId: Int): DBQueryState =
        playlistRepository.insertToPlaylist(playlistId, trackId)
}