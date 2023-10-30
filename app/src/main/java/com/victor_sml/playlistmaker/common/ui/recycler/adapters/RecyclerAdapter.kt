package com.victor_sml.playlistmaker.common.ui.recycler.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.victor_sml.playlistmaker.common.ui.recycler.api.AdapterDelegate
import com.victor_sml.playlistmaker.common.ui.recycler.api.RecyclerItem

class RecyclerAdapter(private val delegates: List<AdapterDelegate>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var layoutManager: RecyclerView.LayoutManager? = null

    private val items: ArrayList<RecyclerItem> = arrayListOf()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        layoutManager = recyclerView.layoutManager
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegates[viewType].getViewHolder(parent, layoutManager)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegates[getItemViewType(position)].bindViewHolder(holder, items[position])
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int =
        delegates.indexOfFirst { delegate -> delegate.forItem(items[position]) }

    fun update(items: List<RecyclerItem>? = null) {
        this.items.clear()
        if (items != null) {
            this.items.addAll(items)
        }
        this.notifyDataSetChanged()
    }
}