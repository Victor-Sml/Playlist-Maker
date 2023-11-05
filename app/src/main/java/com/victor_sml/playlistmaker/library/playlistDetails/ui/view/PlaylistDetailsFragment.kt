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
import android.widget.TextView
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.Constants
import com.victor_sml.playlistmaker.common.domain.models.Track
import com.victor_sml.playlistmaker.common.ui.BottomSheetController
import com.victor_sml.playlistmaker.common.ui.nonBottomNavFragment.NonBottomNavFragmentImpl
import com.victor_sml.playlistmaker.common.ui.recycler.adapters.RecyclerAdapter
import com.victor_sml.playlistmaker.common.ui.recycler.api.RecyclerItem.TrackItem
import com.victor_sml.playlistmaker.common.ui.recycler.delegates.PlaylistDelegate
import com.victor_sml.playlistmaker.common.ui.recycler.delegates.TextLineDelegate
import com.victor_sml.playlistmaker.common.ui.recycler.delegates.TrackDelegate
import com.victor_sml.playlistmaker.common.utils.UtilsUi.doOnApplyWindowInsets
import com.victor_sml.playlistmaker.common.utils.debounce
import com.victor_sml.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.victor_sml.playlistmaker.library.playlistDetails.ui.model.PlaylistUi
import com.victor_sml.playlistmaker.library.playlistDetails.ui.stateholder.PlaylistDetailsViewModel
import com.victor_sml.playlistmaker.main.ui.stateholder.SharedViewModel
import java.io.File
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistDetailsFragment : NonBottomNavFragmentImpl<FragmentPlaylistDetailsBinding>() {
    private val viewModel by viewModel<PlaylistDetailsViewModel>()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var playlistId: Int? = null

    private lateinit var recyclerAdapter: RecyclerAdapter

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

    private fun loadPlaylist() {
        playlistId = arguments?.getInt(PLAYLIST_ID_ARGUMENT)
        playlistId?.let { viewModel.loadPlaylist(it) }
    }

    private fun setStatusBarAppearanceLight(isStatusBarLight: Boolean) {
        windowInsetsController.isAppearanceLightStatusBars = isStatusBarLight
    }

    private fun observeScreenContent() {
        viewModel.getState().observe(viewLifecycleOwner) { playlist ->
            renderViews(playlist)
        }
    }

    private fun initializeUI() {
        applyWindowInsets()
        initRecycler()
        initBottomSheets()
        setListeners()
    }

    private fun applyWindowInsets() {
        with(binding) {
            clRoot.doOnApplyWindowInsets(left = true, top = true, right = true, bottom = true)

            flToolbarContainer.doOnApplyWindowInsets(left = true, top = true, right = true)

            rvTracks.doOnApplyWindowInsets(bottom = true)

            ViewCompat.setOnApplyWindowInsetsListener(binding.llTracks) { view, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

                view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    leftMargin = insets.left
                    rightMargin = insets.right
                }

                windowInsets
            }
        }
    }

    private fun initRecycler() {
        val onTrackClickDebounce: (Track) -> Unit =
            debounce(Constants.CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope) { track ->
                sharedViewModel.passTrack(Constants.TRACK_FOR_PLAYER, track)
                findNavController().navigate(R.id.action_global_player)
            }

        val trackClickListener = object : TrackDelegate.TrackClickListener {
            override fun onTrackClick(track: Track) {
                onTrackClickDebounce(track)
            }
        }

        val trackLongClickListener = object : TrackDelegate.TrackLongClickListener {
            override fun onTrackLongClick(trackId: Int) {
                showDeletionDialog(getString(TRACK_DIALOG_TITLE)) {
                    playlistId?.let { viewModel.deleteFromPlaylist(it, trackId) }
                }
            }
        }

        recyclerAdapter =
            RecyclerAdapter(
                arrayListOf(
                    TrackDelegate(trackClickListener, trackLongClickListener),
                    PlaylistDelegate(),
                    TextLineDelegate()
                )
            )
        binding.rvTracks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTracks.adapter = recyclerAdapter
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

    private fun renderViews(playlist: PlaylistUi) {
        renderDetailArea(playlist)
        renderTracksArea(playlist)
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

    private fun renderTracksArea(playlist: PlaylistUi) {
        if (playlist.tracks.isNullOrEmpty()) {
            showEmptyMassage()
            return
        }

        showTracks(playlist.tracks)
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

    private fun showTracks(tracks: List<TrackItem>) {
        binding.tvWithoutTracksPlaylist.isVisible = false
        binding.rvTracks.isVisible = true
        recyclerAdapter.update(tracks)
    }

    private fun showEmptyMassage() {
        recyclerAdapter.update()
        binding.rvTracks.isVisible = false
        binding.tvWithoutTracksPlaylist.isVisible = true
    }

    private fun showDeletionDialog(message: String, action: () -> Unit) {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(R.layout.dialog_deletion)
            .show()

        val positiveButton = dialog.findViewById<TextView>(R.id.tv_positive_button)
        val negativeButton = dialog.findViewById<TextView>(R.id.tv_negative_button)

        dialog.findViewById<TextView>(R.id.tv_message)?.text = message

        positiveButton?.setOnClickListener {
            action()
            dialog.dismiss()
        }

        negativeButton?.setOnClickListener { dialog.dismiss() }
    }

    companion object {
        const val PLAYLIST_ID_ARGUMENT = "playlistId"

        const val CASE_OF_TRACK_PLURALS_ID = R.plurals.case_of_track
        const val CASE_OF_MINUTE_PLURALS_ID = R.plurals.case_of_minute

        const val TRACK_DIALOG_TITLE = R.string.deletion_of_a_track_dialog_title
    }
}