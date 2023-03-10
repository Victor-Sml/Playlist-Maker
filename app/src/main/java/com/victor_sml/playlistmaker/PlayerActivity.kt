package com.victor_sml.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.victor_sml.playlistmaker.databinding.ActivityPlayerBinding
import com.victor_sml.playlistmaker.utils.dpToPx

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val track = intent.getParcelableExtra<Track>("track")
        setContent(track)

        setListeners()
    }

    private fun setListeners() {
        binding.libraryToolbar.setNavigationOnClickListener {
            this.finish()
        }
    }

    private fun setContent(track: Track?) {
        val artworkCornerRadius = dpToPx(BIG_ARTWORK_RADIUS, this)

        Glide.with(this).load(track?.getCoverArtwork())
            .placeholder(R.drawable.default_artwork)
            .centerCrop()
            .transform(RoundedCorners(artworkCornerRadius))
            .into(binding.ivArtwork)

        binding.run {
            tvTrackName.text = track?.trackName
            tvArtistName.text = track?.artistName
            tvTrackTimeValue.text = track?.trackTime
            tvAlbumValue.text = track?.collectionName
            tvReleaseDataValue.text = track?.releaseDate
            tvGenreValue.text = track?.primaryGenreName
            tvCountryValue.text = track?.country

            tvDuration.text = track?.trackTime
        }
    }

    companion object {
        const val BIG_ARTWORK_RADIUS = 8
    }
}