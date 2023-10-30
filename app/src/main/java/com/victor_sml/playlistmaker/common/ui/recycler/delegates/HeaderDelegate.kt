package com.victor_sml.playlistmaker.common.ui.recycler.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.ui.recycler.api.AdapterDelegate
import com.victor_sml.playlistmaker.common.ui.recycler.api.RecyclerItem

class HeaderDelegate : AdapterDelegate {
    override fun forItem(item: RecyclerItem): Boolean = item is RecyclerItem.Header

    override fun getViewHolder(
        parent: ViewGroup,
        layoutManager: RecyclerView.LayoutManager?
    ): RecyclerView.ViewHolder = HeaderViewHolder(parent)

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: RecyclerItem) {
        val viewHolder = viewHolder as HeaderViewHolder
        val item = item as RecyclerItem.Header

        viewHolder.title.text = item.title
    }

    private inner class HeaderViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parentView.context)
            .inflate(R.layout.recycler_header, parentView, false)
    ) {
        val title: TextView = itemView.findViewById(R.id.recycler_header_title)
    }
}