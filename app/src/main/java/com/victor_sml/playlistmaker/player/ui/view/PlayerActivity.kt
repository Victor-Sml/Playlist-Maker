package com.victor_sml.playlistmaker.player.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.models.TrackUi
import com.victor_sml.playlistmaker.databinding.ActivityPlayerBinding
import com.victor_sml.playlistmaker.player.ui.stateholders.PlayerState.STARTED
import com.victor_sml.playlistmaker.search.ui.view.TRACK_FOR_PLAYER
import com.victor_sml.playlistmaker.common.utils.Utils.dpToPx
import com.victor_sml.playlistmaker.player.ui.stateholders.PlayerState
import com.victor_sml.playlistmaker.player.ui.stateholders.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private val viewModel by viewModel<PlayerViewModel> {
        parametersOf(track?.previewUrl)
    }
    private var track: TrackUi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        track = intent.getParcelableExtra(TRACK_FOR_PLAYER)

        viewModel.getPlayerState().observe(this) { playerState ->
            renderController(playerState)
        }

        viewModel.getPlaybackProgress().observe(this) { progress ->
            updatePlaybackProgress(progress)
        }

        initViews()
        setListeners()
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.getPlayerState().value == STARTED) {
            viewModel.playbackControl()
        }
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
            viewModel.playbackControl()
        }

        binding.libraryToolbar.setNavigationOnClickListener {
            this.finish()
        }
    }

    private fun renderController(state: PlayerState) {
        updateControllerAvailability(state.controllerAvailability)
        updateControllerImage(state.controllerIconId)
    }

    private fun updateControllerAvailability(isEnabled: Boolean) {
        binding.fabPlaybackControl.isEnabled = isEnabled
    }

    private fun updateControllerImage(drawableId: Int) {
        binding.fabPlaybackControl.setImageDrawable(getDrawable(drawableId))
    }

    private fun updatePlaybackProgress(progress: String) {
        binding.tvPlaybackProgress.text = progress
    }

    companion object {
        private const val BIG_ARTWORK_RADIUS_DP = 8
    }
}