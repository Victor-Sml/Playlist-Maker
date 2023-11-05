package com.victor_sml.playlistmaker.common.ui.recycler.api

import com.victor_sml.playlistmaker.common.domain.models.Playlist
import com.victor_sml.playlistmaker.common.domain.models.Track

sealed interface RecyclerItem {
    data class TrackItem(val track: Track) : RecyclerItem

    data class PlaylistItem(val playlist: Playlist) : RecyclerItem

    data class Header(val title: String) : RecyclerItem

    data class Button(val text: String, val callback: () -> Unit) : RecyclerItem

    data class Message(val iconId: Int, val text: String) : RecyclerItem

    data class Space(val heightDp: Int): RecyclerItem

    data class TextLine(val text: String): RecyclerItem
}