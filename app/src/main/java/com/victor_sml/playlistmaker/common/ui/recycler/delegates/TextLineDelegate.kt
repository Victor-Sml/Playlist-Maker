package com.victor_sml.playlistmaker.common.ui.recycler.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.ui.recycler.api.AdapterDelegate
import com.victor_sml.playlistmaker.common.ui.recycler.api.RecyclerItem
import com.victor_sml.playlistmaker.common.ui.recycler.api.RecyclerItem.TextLine

class TextLineDelegate : AdapterDelegate {
    override fun forItem(item: RecyclerItem): Boolean = item is TextLine

    override fun getViewHolder(
        parent: ViewGroup,
        layoutManager: RecyclerView.LayoutManager?
    ): RecyclerView.ViewHolder = TextLineViewHolder(parent)

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: RecyclerItem) {
        val viewHolder = viewHolder as TextLineViewHolder
        val item = item as TextLine

        viewHolder.string.text = item.text
    }

    private inner class TextLineViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parentView.context)
            .inflate(R.layout.recycler_text_line, parentView, false)
    ) {
        val string: TextView = itemView.findViewById(R.id.tv_text)
    }
}