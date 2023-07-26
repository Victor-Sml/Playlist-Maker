package com.victor_sml.playlistmaker.common.utils.recycler.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.utils.recycler.api.AdapterDelegate
import com.victor_sml.playlistmaker.common.utils.recycler.api.RecyclerItem

class ButtonDelegate(private val clickListener: ClickListener) : AdapterDelegate {
    override fun forItem(item: RecyclerItem): Boolean = item is RecyclerItem.Button

    override fun getViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        ButtonViewHolder(parent)

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: RecyclerItem) {
        viewHolder as ButtonViewHolder
        item as RecyclerItem.Button

        with(viewHolder) {
            button.text = item.text
            button.setOnClickListener { clickListener.onButtonClick(item.callback) }
        }
    }

    interface ClickListener {
        fun onButtonClick(callback: () -> Unit)
    }

    private inner class ButtonViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parentView.context)
            .inflate(R.layout.recycler_button, parentView, false)
    ) {
        val button: Button = itemView.findViewById(R.id.btn_recycler_button)
    }
}