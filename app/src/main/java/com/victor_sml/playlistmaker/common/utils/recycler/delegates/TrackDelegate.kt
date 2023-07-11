package com.victor_sml.playlistmaker.common.utils.recycler.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.utils.Utils
import com.victor_sml.playlistmaker.common.models.Track
import com.victor_sml.playlistmaker.common.utils.Utils.toTimeMMSS
import com.victor_sml.playlistmaker.common.utils.recycler.api.AdapterDelegate
import com.victor_sml.playlistmaker.common.utils.recycler.api.RecyclerItem

class TrackDelegate(private val clickListener: TrackClickListener) : AdapterDelegate {
    override fun forItem(item: RecyclerItem): Boolean = item is RecyclerItem.TrackItem

    override fun getViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = TrackViewHolder(parent)

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: RecyclerItem) {
        val trackViewHolder = viewHolder as TrackViewHolder
        val track = item as RecyclerItem.TrackItem
        val context = trackViewHolder.itemView.context

        trackViewHolder.trackName.text = track.track.trackName
        trackViewHolder.artistName.text = track.track.artistName
        trackViewHolder.trackTime.text = track.track.trackTimeMillis?.toTimeMMSS()

        Utils.dpToPx(SMALL_ARTWORK_RADIUS, context).let { radius ->
            Glide.with(context).load(track.track.artworkUrl100)
                .placeholder(R.drawable.default_artwork)
                .centerCrop()
                .transform(RoundedCorners(radius))
                .into(trackViewHolder.artwork)
        }

        viewHolder.itemView.setOnClickListener {
            clickListener.onTrackClick(track.track)
        }
    }

    interface TrackClickListener {
        fun onTrackClick(track: Track)
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