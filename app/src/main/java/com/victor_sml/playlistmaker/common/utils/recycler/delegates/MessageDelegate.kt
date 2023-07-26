package com.victor_sml.playlistmaker.common.utils.recycler.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.utils.recycler.api.RecyclerItem.Message
import com.victor_sml.playlistmaker.common.utils.recycler.api.AdapterDelegate
import com.victor_sml.playlistmaker.common.utils.recycler.api.RecyclerItem

class MessageDelegate : AdapterDelegate {
    override fun forItem(item: RecyclerItem): Boolean = item is Message

    override fun getViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        MessageViewHolder(parent)

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: RecyclerItem) {
        viewHolder as MessageViewHolder
        item as Message
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