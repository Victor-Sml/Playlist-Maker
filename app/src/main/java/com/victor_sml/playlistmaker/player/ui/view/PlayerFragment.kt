package com.victor_sml.playlistmaker.player.ui.view

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.Constants.BIG_ARTWORK_RADIUS_DP
import com.victor_sml.playlistmaker.common.Constants.CLICK_DEBOUNCE_DELAY
import com.victor_sml.playlistmaker.common.Constants.TRACK_FOR_PLAYER
import com.victor_sml.playlistmaker.common.models.Playlist
import com.victor_sml.playlistmaker.common.models.Track
import com.victor_sml.playlistmaker.common.ui.BindingFragment
import com.victor_sml.playlistmaker.common.utils.Utils
import com.victor_sml.playlistmaker.common.utils.Utils.toDateYYYY
import com.victor_sml.playlistmaker.common.utils.Utils.toTimeMMSS
import com.victor_sml.playlistmaker.common.utils.debounce
import com.victor_sml.playlistmaker.common.utils.recycler.adapters.UltimatePlaylistAdapter
import com.victor_sml.playlistmaker.common.utils.recycler.adapters.UltimatePlaylistAdapter.PlaylistClickListener
import com.victor_sml.playlistmaker.databinding.FragmentPlayerBinding
import com.victor_sml.playlistmaker.main.ui.stateholder.SharedViewModel
import com.victor_sml.playlistmaker.player.ui.stateholders.PlayerState
import com.victor_sml.playlistmaker.player.ui.stateholders.PlayerViewModel
import com.victor_sml.playlistmaker.player.ui.stateholders.TrackInsertionState
import com.victor_sml.playlistmaker.player.ui.stateholders.TrackInsertionState.Fail
import com.victor_sml.playlistmaker.player.ui.stateholders.TrackInsertionState.Success
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class PlayerFragment : BindingFragment<FragmentPlayerBinding>() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val viewModel by viewModel<PlayerViewModel> {
        parametersOf(track)
    }

    private lateinit var recyclerAdapter: UltimatePlaylistAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit

    private var track: Track? = null

    private val playlistClickListener = object : PlaylistClickListener {
        override fun onPlaylistClick(playlist: Playlist) {
            onPlaylistClickDebounce(playlist)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.fade)
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentPlayerBinding {
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

        viewModel.getPlaylists().observe(viewLifecycleOwner) { playlists ->
            recyclerAdapter.update(playlists)
        }

        viewModel.getTrackInsertionState().observe(viewLifecycleOwner) { state ->
            processTrackInsertionState(state)
        }

        onPlaylistClickDebounce =
            debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope) { playlist ->
                track?.let { track ->
                    viewModel.onAddToPlaylistClick(playlist, track)
                }
            }

        initViews()
        initBottomSheet()
        initRecycler()
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

    private fun initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bsAddingToPlaylist)
        bottomSheetBehavior.state = STATE_HIDDEN
    }

    private fun initRecycler() {
        val recycler = binding.rwPlaylists
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recyclerAdapter = UltimatePlaylistAdapter(playlistClickListener)
        recycler.adapter = recyclerAdapter
    }

    private fun setListeners() {
        binding.fabPlaybackControl.setOnClickListener {
            viewModel.onPlaybackControlClick()
        }

        binding.fabLike.setOnClickListener {
            viewModel.onLikeClick()
        }

        binding.fabAddToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = STATE_HALF_EXPANDED
        }

        binding.btnNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_global_new_playlist)
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

    private fun processTrackInsertionState(state: TrackInsertionState) {
        showMessage(state.message)
        when (state) {
            is Success -> {
                bottomSheetBehavior.state = STATE_HIDDEN
            }

            is Fail -> {
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}