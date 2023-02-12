package com.victor_sml.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TrackAdapter(val tracklist: ArrayList<Track>) :
    RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracklist[position])
    }

    override fun getItemCount(): Int = tracklist.size

    fun update(tracklist: ArrayList<Track>? = null) {
        this.tracklist.clear()
        if (tracklist != null) this.tracklist.addAll(tracklist)
        this.notifyDataSetChanged()
    }

    class TrackViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parentView.context)
            .inflate(R.layout.tracklist_item_view, parentView, false)
    ) {
        private val artwork: ImageView = itemView.findViewById(R.id.iv_artwork)
        private val trackName: TextView = itemView.findViewById(R.id.tv_track_name)
        private val artistName: TextView = itemView.findViewById(R.id.tv_artistName)
        private val trackTime: TextView = itemView.findViewById(R.id.tv_track_time)

        fun bind(track: Track) {
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

            Glide.with(itemView.context).load(track.artworkUrl100)
                .placeholder(R.drawable.default_artwork)
                .centerCrop()
                .transform(RoundedCorners(2))
                .into(artwork)
        }
    }
}