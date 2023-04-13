package com.victor_sml.playlistmaker.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.victor_sml.playlistmaker.recycler.RecyclerItemType

interface AdapterDelegate {
    fun forItem(item: RecyclerItemType): Boolean
    fun getViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: RecyclerItemType)
}