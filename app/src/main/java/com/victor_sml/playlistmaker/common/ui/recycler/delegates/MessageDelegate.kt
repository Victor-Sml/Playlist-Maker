package com.victor_sml.playlistmaker.common.ui.recycler.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.ui.recycler.api.AdapterDelegate
import com.victor_sml.playlistmaker.common.ui.recycler.api.RecyclerItem

class MessageDelegate : AdapterDelegate {
    override fun forItem(item: RecyclerItem): Boolean = item is RecyclerItem.Message

    override fun getViewHolder(
        parent: ViewGroup,
        layoutManager: LayoutManager?,
    ): RecyclerView.ViewHolder =
        MessageViewHolder(parent)

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: RecyclerItem) {
        viewHolder as MessageViewHolder
        item as RecyclerItem.Message
        viewHolder.messageHolder.setCompoundDrawablesWithIntrinsicBounds(0, item.iconId, 0, 0)
        viewHolder.messageHolder.text = item.text
    }

    private inner class MessageViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parentView.context)
            .inflate(R.layout.recycler_massage, parentView, false)
    ) {
        val messageHolder: TextView = itemView.findViewById(R.id.message_holder)
    }
}