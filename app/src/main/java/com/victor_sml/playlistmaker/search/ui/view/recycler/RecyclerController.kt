package com.victor_sml.playlistmaker.search.ui.view.recycler

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.victor_sml.playlistmaker.search.ui.view.recycler.delegates.ButtonDelegate
import com.victor_sml.playlistmaker.search.ui.view.recycler.delegates.ButtonDelegate.ClickListener
import com.victor_sml.playlistmaker.search.ui.view.recycler.delegates.HeaderDelegate
import com.victor_sml.playlistmaker.search.ui.view.recycler.delegates.MessageDelegate
import com.victor_sml.playlistmaker.search.ui.view.recycler.delegates.TrackDelegate
import com.victor_sml.playlistmaker.search.ui.view.recycler.delegates.TrackDelegate.TrackClickListener
import com.victor_sml.playlistmaker.search.ui.view.recycler.api.RecyclerItem

class RecyclerController(
    recyclerView: RecyclerView,
    trackClickListener: TrackClickListener,
    buttonClickListener: ClickListener
) {
    private val adapter = TrackAdapter(
        delegates = arrayListOf(
            TrackDelegate(trackClickListener),
            ButtonDelegate(buttonClickListener),
            MessageDelegate(),
            HeaderDelegate()
        )
    )

    init {
        recyclerView.layoutManager =
            LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
    }

    fun clearContent() {
        updateContent()
    }

    fun updateContent(content: ArrayList<RecyclerItem>? = null) {
        adapter.update(content)
    }
}