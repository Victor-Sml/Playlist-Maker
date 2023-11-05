package com.victor_sml.playlistmaker.player.ui.view

import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnDrawListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.google.android.material.snackbar.Snackbar
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.Constants.CLICK_DEBOUNCE_DELAY
import com.victor_sml.playlistmaker.common.Constants.DEFAULT_ARTWORK_DRAWABLE_ID
import com.victor_sml.playlistmaker.common.Constants.TRACK_FOR_PLAYER
import com.victor_sml.playlistmaker.common.domain.models.Playlist
import com.victor_sml.playlistmaker.common.domain.models.Track
import com.victor_sml.playlistmaker.common.ui.nonBottomNavFragment.NonBottomNavFragmentImpl
import com.victor_sml.playlistmaker.common.ui.recycler.adapters.RecyclerAdapter
import com.victor_sml.playlistmaker.common.ui.recycler.delegates.PlaylistDelegate
import com.victor_sml.playlistmaker.common.ui.recycler.delegates.PlaylistDelegate.PlaylistClickListener
import com.victor_sml.playlistmaker.common.utils.Utils.toDateYYYY
import com.victor_sml.playlistmaker.common.utils.Utils.toTimeMMSS
import com.victor_sml.playlistmaker.common.utils.UtilsUi.doOnApplyWindowInsets
import com.victor_sml.playlistmaker.common.utils.UtilsUi.setImageFrom
import com.victor_sml.playlistmaker.common.utils.debounce
import com.victor_sml.playlistmaker.databinding.FragmentPlayerBinding
import com.victor_sml.playlistmaker.main.ui.stateholder.SharedViewModel
import com.victor_sml.playlistmaker.player.ui.stateholders.PlayerState
import com.victor_sml.playlistmaker.player.ui.stateholders.PlayerViewModel
import com.victor_sml.playlistmaker.player.ui.stateholders.TrackInsertionState
import com.victor_sml.playlistmaker.player.ui.stateholders.TrackInsertionState.Fail
import com.victor_sml.playlistmaker.player.ui.stateholders.TrackInsertionState.Success
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : NonBottomNavFragmentImpl<FragmentPlayerBinding>() {
    private var track: Track? = null

    private val viewModel by viewModel<PlayerViewModel> { parametersOf(track) }
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val windowInsetsController
        get() = WindowInsetsControllerCompat(
            requireActivity().window,
            requireActivity().window.decorView
        )

    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var bottomSheetController: PlayerBottomSheetController

    private var layoutOnDrawListener = OnDrawListener {
        with(bottomSheetController) {
            setBottomSheetPeekHeight(binding.tvArtistName, GAP_ABOVE_BOTTOM_SHEET_DP)
            setBottomSheetMaxHeight(binding.tbPlayer)
        }
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

        observeScreenContent()
        initializeUi()
    }

    override fun onPause() {
        super.onPause()

        removeListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        setStatusBarAppearanceLight(
            ((resources.configuration.uiMode and UI_MODE_NIGHT_MASK) == UI_MODE_NIGHT_NO)
        )
    }

    private fun setStatusBarAppearanceLight(isStatusBarLight: Boolean) {
        windowInsetsController.isAppearanceLightStatusBars = isStatusBarLight
    }

    private fun observeScreenContent() {
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
    }

    private fun initializeUi() {
        initViews()
        initBottomSheet()
        initRecycler()
        applyWindowInsets()
        setListeners()
    }

    private fun initViews() {
        val setValue: (String?) -> String = {
            it ?: getString(R.string.not_applicable)
        }

        with(binding) {
            ivArtwork.setImageFrom(track?.getCoverArtwork(), DEFAULT_ARTWORK_DRAWABLE_ID)
            tvTrackName.text = setValue(track?.trackName)
            tvArtistName.text = setValue(track?.artistName)
            tvTrackTimeValue.text = setValue(track?.trackTimeMillis?.toTimeMMSS())
            tvAlbumValue.text = setValue(track?.collectionName)
            tvReleaseYearValue.text = setValue(track?.releaseDate?.toDateYYYY())
            tvGenreValue.text = setValue(track?.primaryGenreName)
            tvCountryValue.text = setValue(track?.country)
            binding.tvTitleTop.text = track?.trackName
        }
    }

    private fun initBottomSheet() {
        bottomSheetController = PlayerBottomSheetController(
            binding.bsAddingToPlaylist,
            binding.tbPlayer,
            binding.overlay,
            binding.tvTitleTop
        )

        binding
            .tvTrackName
            .viewTreeObserver
            .addOnDrawListener(layoutOnDrawListener)
    }

    private fun initRecycler() {
        val onPlaylistClickDebounce: (Playlist) -> Unit =
            debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope) { playlist ->
                track?.let { viewModel.onAddToPlaylistClick(playlist, it) }
            }

        val playlistClickListener = object : PlaylistClickListener {
            override fun onPlaylistClick(playlist: Playlist) {
                onPlaylistClickDebounce(playlist)
            }
        }

        recyclerAdapter = RecyclerAdapter(arrayListOf(PlaylistDelegate(playlistClickListener)))

        binding.rvPlaylists.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPlaylists.adapter = recyclerAdapter
    }

    private fun applyWindowInsets() {
        binding.svRoot.doOnApplyWindowInsets(left = true, top = true, right = true, bottom = true)

        binding.tbPlayer.doOnApplyWindowInsets(left = true, top = true, right = true)

        binding.tvTitleTop.doOnApplyWindowInsets(left = true, top = true, right = true)

        binding.rvPlaylists.doOnApplyWindowInsets(bottom = true)

        ViewCompat.setOnApplyWindowInsetsListener(binding.bsAddingToPlaylist) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                rightMargin = insets.right
            }

            windowInsets
        }
    }

    private fun setListeners() {
        binding.fabPlaybackControl.setOnClickListener {
            viewModel.onPlaybackControlClick()
        }

        binding.fabLike.setOnClickListener {
            viewModel.onLikeClick()
        }

        binding.fabAddToPlaylist.setOnClickListener {
            bottomSheetController.bottomSheetBehavior.state = STATE_COLLAPSED
        }

        binding.btnNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_global_playlist_editor)
        }

        binding.tbPlayer.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun removeListeners() {
        binding.tvTrackName.viewTreeObserver.removeOnDrawListener(layoutOnDrawListener)
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
                bottomSheetController.bottomSheetBehavior.state = STATE_HIDDEN
            }

            is Fail -> {
            }
        }
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.svRoot, message, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        const val GAP_ABOVE_BOTTOM_SHEET_DP = 8
    }
}