package com.victor_sml.playlistmaker.common.utils.recycler.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Space
import androidx.recyclerview.widget.RecyclerView
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.utils.Utils
import com.victor_sml.playlistmaker.common.utils.recycler.api.AdapterDelegate
import com.victor_sml.playlistmaker.common.utils.recycler.api.RecyclerItem

class SpaceDelegate : AdapterDelegate {
    override fun forItem(item: RecyclerItem): Boolean = item is RecyclerItem.Space

    override fun getViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = SpaceViewHolder(parent)

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: RecyclerItem) {
        viewHolder as SpaceViewHolder
        item as RecyclerItem.Space

        viewHolder.space.layoutParams.height =
            Utils.dpToPx(item.heightDp, viewHolder.itemView.context)
    }

    private inner class SpaceViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parentView.context)
            .inflate(R.layout.recycler_space, parentView, false)
    ) {
        val space: Space = itemView.findViewById(R.id.recycler_space)
    }
}