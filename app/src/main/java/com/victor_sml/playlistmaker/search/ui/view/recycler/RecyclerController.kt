package com.victor_sml.playlistmaker.search.ui.view.recycler

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.victor_sml.playlistmaker.common.models.TrackUi
import com.victor_sml.playlistmaker.search.ui.view.recycler.ClearButtonDelegate.ClickListener
import com.victor_sml.playlistmaker.search.ui.view.recycler.TrackDelegate.TrackClickListener
import com.victor_sml.playlistmaker.search.ui.view.recycler.api.RecyclerItem

class RecyclerController(
    recyclerView: RecyclerView,
    trackClickListener: TrackClickListener,
    buttonClickListener: ClickListener
) {
    private val adapter = TrackAdapter(
        delegates = arrayListOf(
            TrackDelegate(trackClickListener),
            ClearButtonDelegate(buttonClickListener)
        )
    )

    init {
        recyclerView.layoutManager =
            LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
    }

    fun addTracks(tracks: List<TrackUi>, isHistory: Boolean) {
        if (isHistory)
            addHistory(tracks)
        else
            addSearchResult(tracks)
    }

    private fun addSearchResult(tracks: List<TrackUi>) {
        val items = arrayListOf<RecyclerItem>()
        items.addAll(tracks)
        updateContent(items)
    }

    private fun addHistory(tracks: List<TrackUi>) {
        val items = arrayListOf<RecyclerItem>()
        items.addAll(tracks)
        items.add(ClearButton())
        updateContent(items)
    }

    private fun updateContent(content: ArrayList<RecyclerItem>? = null) {
        adapter.update(content)
    }
}