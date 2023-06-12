package com.victor_sml.playlistmaker.search.ui.view.recycler.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Space
import androidx.recyclerview.widget.RecyclerView
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.search.ui.view.recycler.api.AdapterDelegate
import com.victor_sml.playlistmaker.search.ui.view.recycler.api.RecyclerItem

class SpaceDelegate : AdapterDelegate {
    override fun forItem(item: RecyclerItem): Boolean = item is RecyclerItem.Space

    override fun getViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = SpaceViewHolder(parent)

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: RecyclerItem) {
        (viewHolder as SpaceViewHolder).space.layoutParams.height =
            (item as RecyclerItem.Space).height
    }

    private inner class SpaceViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parentView.context)
            .inflate(R.layout.recycler_space, parentView, false)
    ) {
        val space: Space = itemView.findViewById(R.id.recycler_space)
    }
}