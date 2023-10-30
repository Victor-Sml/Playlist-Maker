package com.victor_sml.playlistmaker.common.ui.recycler.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Space
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.ui.recycler.api.AdapterDelegate
import com.victor_sml.playlistmaker.common.ui.recycler.api.RecyclerItem
import com.victor_sml.playlistmaker.common.utils.Utils.dpToPx

class SpaceDelegate : AdapterDelegate {
    override fun forItem(item: RecyclerItem): Boolean = item is RecyclerItem.Space

    override fun getViewHolder(
        parent: ViewGroup,
        layoutManager: LayoutManager?,
    ): RecyclerView.ViewHolder = SpaceViewHolder(parent)

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: RecyclerItem) {
        viewHolder as SpaceViewHolder
        item as RecyclerItem.Space

        viewHolder.space.layoutParams.height = item.heightDp.dpToPx()
    }

    private inner class SpaceViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parentView.context)
            .inflate(R.layout.recycler_space, parentView, false)
    ) {
        val space: Space = itemView.findViewById(R.id.recycler_space)
    }
}