package com.victor_sml.playlistmaker.common.utils.recycler.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.Constants.BIG_ARTWORK_RADIUS_DP
import com.victor_sml.playlistmaker.common.models.Playlist
import com.victor_sml.playlistmaker.common.utils.Utils
import com.victor_sml.playlistmaker.common.utils.Utils.caseOfTracks
import com.victor_sml.playlistmaker.common.utils.recycler.adapters.UltimatePlaylistAdapter.PlaylistViewHolder
import java.io.File

class UltimatePlaylistAdapter(private val clickListener: PlaylistClickListener) :
    RecyclerView.Adapter<PlaylistViewHolder>() {

    private var layoutManager: LayoutManager? = null

    private val items: ArrayList<Playlist> = arrayListOf()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        layoutManager = recyclerView.layoutManager
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        when (layoutManager) {
            is GridLayoutManager -> return PlaylistViewHolder(parent, PLAYLIST_GRID_LAYOUT_ID)
            else -> return PlaylistViewHolder(parent, PLAYLIST_LINEAR_LAYOUT_ID)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun update(items: ArrayList<Playlist>? = null) {
        this.items.clear()
        if (items != null) {
            this.items.addAll(items)
        }
        this.notifyDataSetChanged()
    }

    interface PlaylistClickListener {
        fun onPlaylistClick(playlist: Playlist)
    }

    inner class PlaylistViewHolder(parentView: ViewGroup, playlistLayoutId: Int) :
        RecyclerView.ViewHolder(
            LayoutInflater.from(parentView.context)
                .inflate(playlistLayoutId, parentView, false)
        ) {
        private val cover: ImageView = itemView.findViewById(R.id.iv_playlist_cover)
        private val title: TextView = itemView.findViewById(R.id.tv_playlist_title)
        private val numberOfTracks: TextView = itemView.findViewById(R.id.tv_number_of_tracks)

        fun bind(playlist: Playlist) {
            title.text = playlist.title

            setCoverImage(playlist)
            setNumberOfTracks(playlist)

            itemView.setOnClickListener {
                clickListener.onPlaylistClick(playlist)
            }
        }

        private fun setCoverImage(playlist: Playlist) {
            var uri: Uri? = null

            playlist.coverPath?.let { coverPath ->
                uri = getImageUri(coverPath)
            }

            Utils.dpToPx(BIG_ARTWORK_RADIUS_DP, cover.context).let { radius ->
                Glide.with(cover.context)
                    .load(uri)
                    .placeholder(R.drawable.default_artwork)
                    .transform(CenterCrop(), RoundedCorners(radius))
                    .into(cover)
            }
        }

        private fun getImageUri(imagePath: String): Uri? {
            val file = File(imagePath)

            return if (file.exists()) file.toUri()
            else null
        }

        private fun setNumberOfTracks(playlist: Playlist) {
            if (playlist.tracks.isNullOrEmpty()) numberOfTracks.text = getNumberOfTracksStr(null)
            else numberOfTracks.text = getNumberOfTracksStr(playlist.tracks.size)
        }

        private fun getNumberOfTracksStr(number: Int?): String {
            val numberOfTracks = itemView.context.resources.getString(NUMBER_OF_TRACKS_STR_ID)
            val emptyPlaylist = itemView.context.resources.getString(EMPTY_PLAYLIST_STR_ID)

            if (number == null) return emptyPlaylist
            return String.format(numberOfTracks, number, caseOfTracks(number))
        }
    }

    companion object {
        const val PLAYLIST_GRID_LAYOUT_ID = R.layout.recycler_grid_playlist_item
        const val PLAYLIST_LINEAR_LAYOUT_ID = R.layout.recycler_linear_playlist_item

        const val NUMBER_OF_TRACKS_STR_ID = R.string.number_of_tracks
        const val EMPTY_PLAYLIST_STR_ID = R.string.empty_playlist
    }
}