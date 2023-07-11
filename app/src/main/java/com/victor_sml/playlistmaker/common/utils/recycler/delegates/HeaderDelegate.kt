package com.victor_sml.playlistmaker.common.utils.recycler.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.utils.recycler.api.AdapterDelegate
import com.victor_sml.playlistmaker.common.utils.recycler.api.RecyclerItem
import com.victor_sml.playlistmaker.common.utils.recycler.api.RecyclerItem.Header

class HeaderDelegate : AdapterDelegate {
    override fun forItem(item: RecyclerItem): Boolean = item is Header

    override fun getViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = HeaderViewHolder(parent)

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: RecyclerItem) {
        val viewHolder = viewHolder as HeaderViewHolder
        val item = item as Header

        viewHolder.title.text = item.title
    }

    private inner class HeaderViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parentView.context)
            .inflate(R.layout.recycler_header, parentView, false)
    ) {
        val title: TextView = itemView.findViewById(R.id.recycler_header_title)
    }
}