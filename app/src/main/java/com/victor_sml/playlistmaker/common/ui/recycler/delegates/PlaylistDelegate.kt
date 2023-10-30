package com.victor_sml.playlistmaker.common.ui.recycler.delegates

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.Constants.BIG_ARTWORK_RADIUS_DP
import com.victor_sml.playlistmaker.common.domain.models.Playlist
import com.victor_sml.playlistmaker.common.ui.recycler.api.AdapterDelegate
import com.victor_sml.playlistmaker.common.ui.recycler.api.RecyclerItem
import com.victor_sml.playlistmaker.common.ui.recycler.api.RecyclerItem.PlaylistItem
import com.victor_sml.playlistmaker.common.utils.Utils.dpToPx
import com.victor_sml.playlistmaker.common.utils.Utils.toNumberOfTracksString
import java.io.File

class PlaylistDelegate(private val clickListener: PlaylistClickListener? = null) : AdapterDelegate {
    override fun forItem(item: RecyclerItem): Boolean = item is PlaylistItem

    override fun getViewHolder(
        parent: ViewGroup,
        layoutManager: RecyclerView.LayoutManager?,
    ): RecyclerView.ViewHolder {
        return when (layoutManager) {
            is GridLayoutManager -> PlaylistViewHolder(parent, PLAYLIST_GRID_LAYOUT_ID)
            else -> PlaylistViewHolder(parent, PLAYLIST_LINEAR_LAYOUT_ID)
        }
    }

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: RecyclerItem) {
        viewHolder as PlaylistViewHolder
        item as PlaylistItem
        viewHolder.bind(item.playlist)
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
            numberOfTracks.text = getNumberOfTracksStr(playlist.numberOfTracks)

            setCoverImage(playlist)

            itemView.setOnClickListener {
                clickListener?.onPlaylistClick(playlist)
            }
        }

        private fun setCoverImage(playlist: Playlist) {
            var uri: Uri? = null

            playlist.coverPath?.let { coverPath ->
                uri = getImageUri(coverPath)
            }

            Glide.with(cover.context)
                .load(uri)
                .placeholder(R.drawable.placeholder)
                .transform(CenterCrop(), RoundedCorners(BIG_ARTWORK_RADIUS_DP.dpToPx()))
                .into(cover)
        }

        private fun getImageUri(imagePath: String): Uri? {
            val file = File(imagePath)

            return if (file.exists()) file.toUri()
            else null
        }

        private fun getNumberOfTracksStr(number: Int): String {
            val emptyPlaylist =
                itemView.context.resources.getString(EMPTY_PLAYLIST_STR_ID)

            if (number == 0) return emptyPlaylist
            return number.toNumberOfTracksString(itemView.context)
        }
    }

    companion object {
        const val PLAYLIST_GRID_LAYOUT_ID = R.layout.recycler_grid_playlists_item
        const val PLAYLIST_LINEAR_LAYOUT_ID = R.layout.recycler_linear_playlists_item

        const val EMPTY_PLAYLIST_STR_ID = R.string.empty_playlist
    }
}