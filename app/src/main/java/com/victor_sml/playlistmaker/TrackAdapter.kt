package com.victor_sml.playlistmaker

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(private val delegates: List<AdapterDelegate>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items: ArrayList<RecyclerItemType> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        delegates[viewType].getViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegates[getItemViewType(position)].bindViewHolder(holder, items[position])
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int =
        delegates.indexOfFirst { delegate -> delegate.forItem(items[position]) }

    fun update(
        tracks: ArrayList<Track>,
        isHistory: Boolean
    ) {
        items.clear()
        if (isHistory) {
            items.addAll(tracks)
            items.add(ClearButton())
        } else {
            items.addAll(tracks)
        }
        this.notifyDataSetChanged()
    }

    fun update() {
        items.clear()
        this.notifyDataSetChanged()
    }
}
