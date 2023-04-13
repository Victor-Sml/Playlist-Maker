package com.victor_sml.playlistmaker.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.domain.Track
import com.victor_sml.playlistmaker.TracksHandler
import com.victor_sml.playlistmaker.Utils.dpToPx

class TrackDelegate(
    private val tracksHandler: TracksHandler,
    private val clickListener: TrackClickListener
) : AdapterDelegate {

    override fun forItem(item: RecyclerItemType): Boolean = item is Track

    override fun getViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = TrackViewHolder(parent)

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: RecyclerItemType) {
        val trackViewHolder = viewHolder as TrackViewHolder
        val track = item as Track
        val context = trackViewHolder.itemView.context

        trackViewHolder.trackName.text = track.trackName
        trackViewHolder.artistName.text = track.artistName
        trackViewHolder.trackTime.text = track.trackTime

        dpToPx(SMALL_ARTWORK_RADIUS, context).let { radius ->
            Glide.with(context).load(track.artworkUrl100)
                .placeholder(R.drawable.default_artwork)
                .centerCrop()
                .transform(RoundedCorners(radius))
                .into(trackViewHolder.artwork)
        }

        viewHolder.itemView.setOnClickListener {
            clickListener.onTrackClick(tracksHandler, track)
        }
    }

    interface TrackClickListener {
        fun onTrackClick(handler: TracksHandler, track: Track)
    }

    private inner class TrackViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parentView.context)
            .inflate(R.layout.tracklist_item_view, parentView, false)
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