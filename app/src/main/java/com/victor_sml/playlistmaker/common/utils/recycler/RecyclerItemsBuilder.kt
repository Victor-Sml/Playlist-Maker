package com.victor_sml.playlistmaker.common.utils.recycler

import com.victor_sml.playlistmaker.common.models.Track
import com.victor_sml.playlistmaker.common.utils.recycler.api.RecyclerItem
import com.victor_sml.playlistmaker.common.utils.recycler.api.RecyclerItem.Button
import com.victor_sml.playlistmaker.common.utils.recycler.api.RecyclerItem.Header
import com.victor_sml.playlistmaker.common.utils.recycler.api.RecyclerItem.Message
import com.victor_sml.playlistmaker.common.utils.recycler.api.RecyclerItem.Space

class RecyclerItemsBuilder {
    private var recyclerItems: ArrayList<RecyclerItem> = arrayListOf()

    private fun reset() {
        recyclerItems = arrayListOf()
    }

    fun addHeader(title: String) = apply { recyclerItems.add(Header(title)) }

    fun addTracks(tracks: List<Track>) =
        apply {
            recyclerItems.addAll(tracks.map { RecyclerItem.TrackItem(it) })
        }

    fun addMessage(iconId: Int, text: String) =
        apply {
            recyclerItems.add(Message(iconId, text))
        }

    fun addButton(text: String, action: () -> Unit) =
        apply {
            recyclerItems.add(Button(text, action))
        }

    fun addSpace(heightDp: Int) = apply { recyclerItems.add(Space(heightDp)) }

    fun getItems(): ArrayList<RecyclerItem> {
        val items = recyclerItems
        reset()
        return items
    }
}