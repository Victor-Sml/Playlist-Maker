package com.victor_sml.playlistmaker.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.TracksHandler

class ClearButtonDelegate(
    private val tracksHandler: TracksHandler,
    private val buttonClickListener: ClearButtonClickListener
) : AdapterDelegate {
    override fun forItem(item: RecyclerItemType): Boolean = item is ClearButton

    override fun getViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        ButtonViewHolder(parent)

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: RecyclerItemType) {
        val buttonViewHolder = viewHolder as ButtonViewHolder

        buttonViewHolder.clearButton.setOnClickListener {
            buttonClickListener.onButtonClick(tracksHandler)
        }
    }

    interface ClearButtonClickListener {
        fun onButtonClick(handler: TracksHandler)
    }

    private inner class ButtonViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parentView.context)
            .inflate(R.layout.clear_button, parentView, false)
    ) {
        val clearButton: Button = itemView.findViewById(R.id.btn_clearHistory)
    }
}