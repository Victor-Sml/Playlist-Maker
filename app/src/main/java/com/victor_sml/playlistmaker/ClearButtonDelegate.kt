package com.victor_sml.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class ClearButtonDelegate(private val tracksHandler: TracksHandler) : AdapterDelegate {
    override fun forItem(item: RecyclerItemType): Boolean = item is ClearButton

    override fun getViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        ButtonViewHolder(parent)

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: RecyclerItemType) {
        val buttonViewHolder = viewHolder as ButtonViewHolder

        buttonViewHolder.clearButton.setOnClickListener {
            tracksHandler.clearHistory()
        }
    }

    private inner class ButtonViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parentView.context)
            .inflate(R.layout.clear_button, parentView, false)
    ) {
        val clearButton: Button = itemView.findViewById(R.id.btn_clearHistory)
    }
}