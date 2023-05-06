package com.victor_sml.playlistmaker.search.ui.view.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.search.ui.view.recycler.api.AdapterDelegate
import com.victor_sml.playlistmaker.search.ui.view.recycler.api.RecyclerItem

class ClearButtonDelegate(
    private val clickListener: ClickListener
) : AdapterDelegate {
    override fun forItem(item: RecyclerItem): Boolean = item is ClearButton

    override fun getViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        ButtonViewHolder(parent)

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: RecyclerItem) {
        val buttonViewHolder = viewHolder as ButtonViewHolder

        buttonViewHolder.clearButton.setOnClickListener {
            clickListener.onButtonClick()
        }
    }

    interface ClickListener {
        fun onButtonClick()
    }

    private inner class ButtonViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parentView.context)
            .inflate(R.layout.clear_button, parentView, false)
    ) {
        val clearButton: Button = itemView.findViewById(R.id.btn_clearHistory)
    }
}
