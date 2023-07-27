package com.victor_sml.playlistmaker.library.domain.api

import com.victor_sml.playlistmaker.common.models.Playlist
import com.victor_sml.playlistmaker.common.utils.DBQueryState
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun addPlaylist(playlist: Playlist): DBQueryState

    suspend fun loadPlaylists(): Flow<List<Playlist>>

    suspend fun insertToPlaylist(playlistId: Int, trackId: Int): DBQueryState
}