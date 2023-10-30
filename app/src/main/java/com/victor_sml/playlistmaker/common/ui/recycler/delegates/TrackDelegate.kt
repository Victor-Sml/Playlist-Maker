package com.victor_sml.playlistmaker.common.ui.recycler.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.domain.models.Track
import com.victor_sml.playlistmaker.common.ui.recycler.api.AdapterDelegate
import com.victor_sml.playlistmaker.common.ui.recycler.api.RecyclerItem
import com.victor_sml.playlistmaker.common.utils.Utils.dpToPx
import com.victor_sml.playlistmaker.common.utils.Utils.toTimeMMSS

class TrackDelegate(
    private val clickListener: TrackClickListener,
    private val longClickListener: TrackLongClickListener? = null,
) : AdapterDelegate {
    override fun forItem(item: RecyclerItem): Boolean = item is RecyclerItem.TrackItem

    override fun getViewHolder(
        parent: ViewGroup,
        layoutManager: LayoutManager?,
    ): RecyclerView.ViewHolder = TrackViewHolder(parent)

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: RecyclerItem) {
        val trackViewHolder = viewHolder as TrackViewHolder
        val track = item as RecyclerItem.TrackItem
        val context = trackViewHolder.itemView.context

        trackViewHolder.trackName.text = track.track.trackName
        trackViewHolder.artistName.text = track.track.artistName
        trackViewHolder.trackTime.text = track.track.trackTimeMillis?.toTimeMMSS()

        Glide.with(context).load(track.track.artworkUrl100)
            .placeholder(R.drawable.default_artwork)
            .centerCrop()
            .transform(RoundedCorners(SMALL_ARTWORK_RADIUS.dpToPx()))
            .into(trackViewHolder.artwork)

        viewHolder.itemView.setOnClickListener {
            clickListener.onTrackClick(track.track)
        }

        if (longClickListener != null) {
            viewHolder.itemView.setOnLongClickListener {
                longClickListener.onTrackLongClick(track.track.trackId)
                return@setOnLongClickListener true
            }
        }
    }

    interface TrackClickListener {
        fun onTrackClick(track: Track)
    }

    interface TrackLongClickListener {
        fun onTrackLongClick(trackId: Int)
    }

    private inner class TrackViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parentView.context)
            .inflate(R.layout.recycler_track_item, parentView, false)
    ) {
        val artwork: ImageView = itemView.findViewById(R.id.iv_artwork)
        val trackName: TextView = itemView.findViewById(R.id.tv_track_name)
        val artistName: TextView = itemView.findViewById(R.id.tv_artistName)
        val trackTime: TextView = itemView.findViewById(R.id.tv_track_time)
    }

    companion object {
        const val SMALL_ARTWORK_RADIUS = 2
    }
}