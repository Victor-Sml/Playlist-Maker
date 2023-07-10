package com.victor_sml.playlistmaker.search.ui.view.recycler.api

import com.victor_sml.playlistmaker.common.models.Track

sealed interface RecyclerItem {
    data class TrackItem(val track: Track) : RecyclerItem
    data class Header(val title: String) : RecyclerItem
    data class Button(val text: String, val callback: () -> Unit) : RecyclerItem
    data class Message(val iconId: Int, val text: String) : RecyclerItem
    data class Space(val heightDp: Int): RecyclerItem
}