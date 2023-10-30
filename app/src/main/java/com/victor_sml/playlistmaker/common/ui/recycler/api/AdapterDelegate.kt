package com.victor_sml.playlistmaker.common.ui.recycler.api

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager

interface AdapterDelegate {
    fun forItem(item: RecyclerItem): Boolean
    fun getViewHolder(parent: ViewGroup, layoutManager: LayoutManager?): RecyclerView.ViewHolder
    fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: RecyclerItem)
}