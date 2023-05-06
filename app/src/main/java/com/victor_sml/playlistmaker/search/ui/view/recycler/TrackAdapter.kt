package com.victor_sml.playlistmaker.search.ui.view.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.victor_sml.playlistmaker.search.ui.view.recycler.api.AdapterDelegate
import com.victor_sml.playlistmaker.search.ui.view.recycler.api.RecyclerItem

class TrackAdapter(private val delegates: List<AdapterDelegate>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items: ArrayList<RecyclerItem> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        delegates[viewType].getViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegates[getItemViewType(position)].bindViewHolder(holder, items[position])
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int =
        delegates.indexOfFirst { delegate -> delegate.forItem(items[position]) }

    fun update(items: ArrayList<RecyclerItem>? = null) {
        this.items.clear()
        if (items != null) {
            this.items.addAll(items)
        }
        this.notifyDataSetChanged()
    }
}