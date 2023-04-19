package com.victor_sml.playlistmaker.presentation.ui.player

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.victor_sml.playlistmaker.Creator.providePlayerPresenter
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.TRACK_FOR_PLAYER
import com.victor_sml.playlistmaker.domain.models.Track
import com.victor_sml.playlistmaker.Utils.dpToPx
import com.victor_sml.playlistmaker.databinding.ActivityPlayerBinding
import com.victor_sml.playlistmaker.presentation.player.api.PlayerPresenter
import com.victor_sml.playlistmaker.presentation.player.api.PlayerView

class PlayerActivity : AppCompatActivity(), PlayerView {
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var presenter: PlayerPresenter
    private var track: Track? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        track = intent.getParcelableExtra(TRACK_FOR_PLAYER)

        initViews()
        setListeners()

        presenter = providePlayerPresenter(
            view = this,
            trackSource = track?.previewUrl.toString())
    }

    override fun onPause() {
        super.onPause()
        presenter.onViewPaused()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewDestroyed()
    }

    private fun initViews() {
        val artworkCornerRadius = dpToPx(BIG_ARTWORK_RADIUS_DP, this)

        Glide.with(this).load(track?.getCoverArtwork())
            .placeholder(R.drawable.default_artwork)
            .centerCrop()
            .transform(RoundedCorners(artworkCornerRadius))
            .into(binding.ivArtwork)

        // Заменяем пустые поля из track на "n/a"
        val setValue: (String?) -> String = {
            it ?: getString(R.string.not_applicable)
        }

        binding.run {
            tvTrackName.text = setValue(track?.trackName)
            tvArtistName.text = setValue(track?.artistName)
            tvTrackTimeValue.text = setValue(track?.trackTime)
            tvAlbumValue.text = setValue(track?.collectionName)
            tvReleaseDataValue.text = setValue(track?.releaseDate)
            tvGenreValue.text = setValue(track?.primaryGenreName)
            tvCountryValue.text = setValue(track?.country)
        }
    }

    private fun setListeners() {
        binding.fabPlaybackControl.setOnClickListener {
            presenter.playbackControl()
        }

        binding.libraryToolbar.setNavigationOnClickListener {
            this.finish()
        }
    }

    override fun updateControllerAvailability(isEnabled: Boolean) {
        binding.fabPlaybackControl.isEnabled = isEnabled
    }

    override fun updateControllerImage(drawableId: Int) {
        binding.fabPlaybackControl.setImageDrawable(getDrawable(drawableId))
    }

    override fun updatePlaybackProgress(progress: String) {
        binding.tvPlaybackProgress.text = progress
    }

    companion object {
        private const val BIG_ARTWORK_RADIUS_DP = 8
    }
}