package com.victor_sml.playlistmaker.common.domain.api.playlists

import com.victor_sml.playlistmaker.common.domain.models.Playlist
import com.victor_sml.playlistmaker.common.domain.models.PlaylistWithTracks
import com.victor_sml.playlistmaker.common.utils.DBQueryState
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun savePlaylist(playlist: Playlist): DBQueryState

    suspend fun insertToPlaylist(playlistId: Int, trackId: Int): DBQueryState

    suspend fun deleteFromPlaylist(playlistId: Int, trackId: Int)

    suspend fun loadPlaylist(playlistId: Int): Flow<PlaylistWithTracks>

    suspend fun loadPlaylists(): Flow<List<Playlist>?>

    suspend fun deletePlaylist(playlistId: Int)
}