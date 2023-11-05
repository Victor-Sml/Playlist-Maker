package com.victor_sml.playlistmaker.library.playlistDetails.ui.view

import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnDrawListener
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.ui.BottomSheetController
import com.victor_sml.playlistmaker.common.ui.nonBottomNavFragment.NonBottomNavFragmentImpl
import com.victor_sml.playlistmaker.common.utils.UtilsUi.doOnApplyWindowInsets
import com.victor_sml.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.victor_sml.playlistmaker.library.playlistDetails.ui.model.PlaylistUi
import com.victor_sml.playlistmaker.library.playlistDetails.ui.stateholder.PlaylistDetailsViewModel
import java.io.File
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistDetailsFragment : NonBottomNavFragmentImpl<FragmentPlaylistDetailsBinding>() {
    private val viewModel by viewModel<PlaylistDetailsViewModel>()

    private var playlistId: Int? = null
    private var playlistUi: PlaylistUi? = null

    private lateinit var bottomSheetControllerTrack: BottomSheetController

    private val windowInsetsController
        get() = WindowInsetsControllerCompat(
            requireActivity().window,
            requireActivity().window.decorView
        )

    private var layoutOnDrawListener = OnDrawListener {
        with(bottomSheetControllerTrack) {
            setBottomSheetMaxHeight(binding.tbPlaylist)
            setBottomSheetPeekHeight(binding.ivSharePlaylist)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadPlaylist()
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentPlaylistDetailsBinding {
        return FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStatusBarAppearanceLight(true)
        observeScreenContent()
        initializeUI()
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

    private fun loadPlaylist() {
        playlistId = arguments?.getInt(PLAYLIST_ID_ARGUMENT)
        playlistId?.let { viewModel.loadPlaylist(it) }
    }

    private fun observeScreenContent() {
        viewModel.getState().observe(viewLifecycleOwner) { playlist ->
            playlistUi = playlist
            renderViews(playlist)
        }
    }

    private fun renderViews(playlist: PlaylistUi) {
        renderDetailArea(playlist)
    }

    private fun renderDetailArea(playlist: PlaylistUi) {
        with(playlist) {
            binding.tvTitleTop.text = title
            binding.tvDescription.isVisible = !description.isNullOrEmpty()
            binding.tvPlaylistTitle.text = title
            binding.tvDescription.text = description

            binding.totalDuration.text =
                resources.getQuantityString(CASE_OF_MINUTE_PLURALS_ID, totalDuration, totalDuration)

            binding.tvNumberOfTracks.text =
                resources.getQuantityString(
                    CASE_OF_TRACK_PLURALS_ID,
                    numberOfTracks,
                    numberOfTracks
                )

            binding.ivPlaylistCover.setCoverImage(coverPath)
        }
    }

    private fun ImageView.setCoverImage(coverPath: String?) {
        var uri: Uri? = null

        coverPath?.let { coverPath ->
            uri = getImageUri(coverPath)
        }

        Glide.with(this.context)
            .load(uri)
            .placeholder(R.drawable.placeholder)
            .transform(CenterCrop())
            .into(this)
    }

    private fun getImageUri(imagePath: String): Uri? {
        val file = File(imagePath)

        return if (file.exists()) file.toUri()
        else null
    }

    private fun initializeUI() {
        applyWindowInsets()
        initBottomSheets()
        setListeners()
    }

    private fun applyWindowInsets() {
        with(binding) {
            clRoot.doOnApplyWindowInsets(left = true, top = true, right = true, bottom = true)

            flToolbarContainer.doOnApplyWindowInsets(left = true, top = true, right = true)

            llTracks.doOnApplyWindowInsets(left = true, right = true)
        }
    }

    private fun initBottomSheets() {
        initBottomSheetTrack()
    }

    private fun initBottomSheetTrack() {
        bottomSheetControllerTrack = TracksBottomSheetController(
            binding.llTracks,
            binding.overlay,
            binding.tbPlaylist,
            binding.tvTitleTop
        )
    }

    private fun setListeners() {
        binding.tbPlaylist.setNavigationOnClickListener { findNavController().popBackStack() }

        binding
            .ivSharePlaylist
            .viewTreeObserver
            .addOnDrawListener(layoutOnDrawListener)
    }

    private fun removeListeners() {
        binding.clRoot.viewTreeObserver.removeOnDrawListener(layoutOnDrawListener)
    }

    companion object {
        const val PLAYLIST_ID_ARGUMENT = "playlistId"

        const val CASE_OF_TRACK_PLURALS_ID = R.plurals.case_of_track
        const val CASE_OF_MINUTE_PLURALS_ID = R.plurals.case_of_minute
    }
}