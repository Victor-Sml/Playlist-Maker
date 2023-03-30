package com.victor_sml.playlistmaker

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.victor_sml.playlistmaker.utils.dpToPx

class TrackDelegate(
    private val tracksHandler: TracksHandler
) : AdapterDelegate {
    private val context: Context = tracksHandler.recyclerView.context

    override fun forItem(item: RecyclerItemType): Boolean = item is Track

    override fun getViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = TrackViewHolder(parent)

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: RecyclerItemType) {
        val trackViewHolder = viewHolder as TrackViewHolder
        val track = item as Track

        trackViewHolder.trackName.text = track.trackName
        trackViewHolder.artistName.text = track.artistName
        trackViewHolder.trackTime.text = track.trackTime

        val artworkCornerRadius = dpToPx(SMALL_ARTWORK_RADIUS, context)
        Glide.with(context).load(track.artworkUrl100)
            .placeholder(R.drawable.default_artwork)
            .centerCrop()
            .transform(RoundedCorners(artworkCornerRadius))
            .into(trackViewHolder.artwork)

        viewHolder.itemView.setOnClickListener {
            tracksHandler.saveTrack(track)

            val libraryIntent = Intent(context, PlayerActivity::class.java)
            libraryIntent.putExtra("track", track)
            context.startActivity(libraryIntent)
        }
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