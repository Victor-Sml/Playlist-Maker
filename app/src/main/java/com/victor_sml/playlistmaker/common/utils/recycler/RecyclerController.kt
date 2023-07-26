package com.victor_sml.playlistmaker.common.utils.recycler

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.victor_sml.playlistmaker.common.utils.recycler.api.AdapterDelegate
import com.victor_sml.playlistmaker.common.utils.recycler.api.RecyclerController
import com.victor_sml.playlistmaker.common.utils.recycler.api.RecyclerItem

class RecyclerControllerIml(recyclerView: RecyclerView, delegates: List<AdapterDelegate>) :
    RecyclerController {

    private val adapter = TrackAdapter(delegates)

    init {
        recyclerView.layoutManager =
            LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
    }

    override fun clearContent() {
        updateContent()
    }

    override fun updateContent(content: ArrayList<RecyclerItem>?) {
        adapter.update(content)
    }
}