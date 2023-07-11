package com.victor_sml.playlistmaker.player.ui.view

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.Constants.TRACK_FOR_PLAYER
import com.victor_sml.playlistmaker.common.models.Track
import com.victor_sml.playlistmaker.common.ui.BindingFragment
import com.victor_sml.playlistmaker.common.utils.Utils
import com.victor_sml.playlistmaker.common.utils.Utils.toDateYYYY
import com.victor_sml.playlistmaker.common.utils.Utils.toTimeMMSS
import com.victor_sml.playlistmaker.databinding.FragmentPlayerBinding
import com.victor_sml.playlistmaker.main.ui.stateholder.SharedViewModel
import com.victor_sml.playlistmaker.player.ui.stateholders.PlayerState
import com.victor_sml.playlistmaker.player.ui.stateholders.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : BindingFragment<FragmentPlayerBinding>() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val viewModel by viewModel<PlayerViewModel> {
        parametersOf(track)
    }

    private var track: Track? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.fade)
    }

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPlayerBinding {
        return FragmentPlayerBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        track = sharedViewModel.getTrack(TRACK_FOR_PLAYER)

        viewModel.getPlayerState().observe(viewLifecycleOwner) { playerState ->
            renderController(playerState)
        }

        viewModel.getPlaybackProgress().observe(viewLifecycleOwner) { progress ->
            updatePlaybackProgress(progress)
        }

        viewModel.getFavoriteState().observe(viewLifecycleOwner) { (iconId, colorId) ->
            renderLikeButton(iconId, colorId)
        }

        initViews()
        setListeners()
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.getPlayerState().value == PlayerState.STARTED) {
            viewModel.onPlaybackControlClick()
        }
    }

    private fun initViews() {
        val artworkCornerRadius = Utils.dpToPx(BIG_ARTWORK_RADIUS_DP, requireContext())

        Glide.with(this).load(track?.getCoverArtwork())
            .placeholder(R.drawable.default_artwork)
            .centerCrop()
            .transform(RoundedCorners(artworkCornerRadius))
            .into(binding.ivArtwork)

        val setValue: (String?) -> String = {
            it ?: getString(R.string.not_applicable)
        }

        binding.run {
            tvTrackName.text = setValue(track?.trackName)
            tvArtistName.text = setValue(track?.artistName)
            tvTrackTimeValue.text = setValue(track?.trackTimeMillis?.toTimeMMSS())
            tvAlbumValue.text = setValue(track?.collectionName)
            tvReleaseYearValue.text = setValue(track?.releaseDate?.toDateYYYY())
            tvGenreValue.text = setValue(track?.primaryGenreName)
            tvCountryValue.text = setValue(track?.country)
        }
    }

    private fun setListeners() {
        binding.fabPlaybackControl.setOnClickListener {
            viewModel.onPlaybackControlClick()
        }

        binding.fabLike.setOnClickListener {
            viewModel.onLikeClick()
        }
    }

    private fun renderLikeButton(iconId: Int, colorId: Int) {
        binding.fabLike.apply {
            setImageDrawable(requireContext().getDrawable(iconId))
            setColorFilter(requireContext().getColor(colorId))
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
        binding.fabPlaybackControl.setImageDrawable(requireContext().getDrawable(drawableId))
    }

    private fun updatePlaybackProgress(progress: String) {
        binding.tvPlaybackProgress.text = progress
    }

    companion object {
        private const val BIG_ARTWORK_RADIUS_DP = 8
    }
}