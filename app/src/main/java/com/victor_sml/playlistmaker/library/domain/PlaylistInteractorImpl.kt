package com.victor_sml.playlistmaker.library.domain

import com.victor_sml.playlistmaker.common.models.Playlist
import com.victor_sml.playlistmaker.common.utils.DBQueryState
import com.victor_sml.playlistmaker.library.domain.api.PlaylistInteractor
import com.victor_sml.playlistmaker.library.domain.api.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {
    override suspend fun addPlaylist(playlist: Playlist): DBQueryState =
        playlistRepository.addPlaylist(playlist)

    override suspend fun loadPlaylist(): Flow<List<Playlist>> =
        playlistRepository.loadPlaylists()

    override suspend fun insertToPlaylist(playlistId: Int, trackId: Int): DBQueryState =
        playlistRepository.insertToPlaylist(playlistId, trackId)
}