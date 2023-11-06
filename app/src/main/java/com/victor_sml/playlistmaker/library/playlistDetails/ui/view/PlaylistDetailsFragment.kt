package com.victor_sml.playlistmaker.library.playlistDetails.ui.view

import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnDrawListener
import android.view.WindowInsets
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.core.graphics.Insets
import androidx.core.net.toUri
import androidx.core.os.bundleOf
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
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
import com.victor_sml.playlistmaker.common.utils.Utils.toNumberOfTracksString
import com.victor_sml.playlistmaker.common.utils.Utils.toTimeMMSS
import com.victor_sml.playlistmaker.common.utils.UtilsUi.doOnApplyWindowInsets
import com.victor_sml.playlistmaker.common.utils.debounce
import com.victor_sml.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.victor_sml.playlistmaker.library.playlistDetails.ui.model.PlaylistUi
import com.victor_sml.playlistmaker.library.playlistDetails.ui.stateholder.PlaylistDetailsViewModel
import com.victor_sml.playlistmaker.library.playlistDetails.ui.view.bottomSheetControllers.BottomSheetControllerMenu
import com.victor_sml.playlistmaker.library.playlistDetails.ui.view.bottomSheetControllers.BottomSheetControllerTracks
import com.victor_sml.playlistmaker.library.playlistEditor.ui.view.PlaylistEditorFragment.Companion.PLAYLIST_FOR_EDITING
import com.victor_sml.playlistmaker.main.ui.stateholder.SharedViewModel
import java.io.File
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistDetailsFragment : NonBottomNavFragmentImpl<FragmentPlaylistDetailsBinding>() {
    private val viewModel by viewModel<PlaylistDetailsViewModel>()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var playlistId: Int? = null
    private var playlistUi: PlaylistUi? = null

    private lateinit var recyclerAdapter: RecyclerAdapter

    private lateinit var bottomSheetControllerTrack: BottomSheetController
    private lateinit var bottomSheetControllerMenu: BottomSheetController

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

        with(bottomSheetControllerMenu) {
            setBottomSheetMaxHeight(binding.tvPlaylistTitle, GAP_ABOVE_BOTTOM_SHEET_DP)
            setBottomSheetPeekHeight(binding.tvPlaylistTitle, GAP_ABOVE_BOTTOM_SHEET_DP)
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
            playlistUi = playlist
            renderViews(playlist)
            setMenuListeners(playlist)
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
            clRoot.doOnApplyWindowInsets(left = true, right = true, bottom = true) { view, insets ->
                view.setMarginTop(insets)
            }

            flToolbarContainer.doOnApplyWindowInsets(left = true, right = true) { view, insets ->
                view.setMarginTop(insets)
            }

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
        initBottomSheetMenu()
    }

    private fun initBottomSheetTrack() {
        bottomSheetControllerTrack = BottomSheetControllerTracks(
            binding.llTracks,
            binding.overlay,
            binding.tbPlaylist,
            binding.tvTitleTop
        )
    }

    private fun initBottomSheetMenu() {
        bottomSheetControllerMenu = BottomSheetControllerMenu(
            binding.clMenu,
            binding.overlay,
            binding.tbPlaylist,
            bottomSheetControllerTrack
        )
    }

    private fun setListeners() {
        binding.tbPlaylist.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.ivSharePlaylist.setOnClickListener {
            playlistUi?.let { onSharePlaylistClicked(it) }
        }

        binding.ivMenu.setOnClickListener {
            bottomSheetControllerMenu.bottomSheetBehavior.state =
                BottomSheetBehavior.STATE_COLLAPSED

            binding.overlay.setOnClickListener {
                bottomSheetControllerMenu.bottomSheetBehavior.state =
                    BottomSheetBehavior.STATE_HIDDEN
            }
        }

        binding
            .ivSharePlaylist
            .viewTreeObserver
            .addOnDrawListener(layoutOnDrawListener)
    }

    private fun setMenuListeners(playlist: PlaylistUi) {
        val onEditPlaylistClickDebounce: (Bundle) -> Unit =
            debounce(Constants.CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope) { playlist ->
                findNavController().navigate(R.id.action_global_playlist_editor, playlist)
            }

        binding.tvMenuEditPlaylist.setOnClickListener {
            onEditPlaylistClickDebounce(bundleOf(PLAYLIST_FOR_EDITING to playlist.toPlaylist()))
        }

        binding.tvMenuSharePlaylist.setOnClickListener {
            onSharePlaylistClicked(playlist)
        }

        binding.tvMenuDeletePlaylist.setOnClickListener {
            showDeletionDialog(String.format(getString(PLAYLIST_DIALOG_TITLE), playlist.title)) {
                viewModel.deletePlaylist(playlist.id)
                findNavController().popBackStack()
            }
        }
    }

    private fun removeListeners() {
        binding.clRoot.viewTreeObserver.removeOnDrawListener(layoutOnDrawListener)
    }

    private fun renderViews(playlist: PlaylistUi) {
        renderDetailArea(playlist)
        renderTracksArea(playlist)
        renderMenuArea(playlist)
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

    private fun renderMenuArea(playlist: PlaylistUi) {
        with(binding) {
            tvMenuPlaylistTitle.text = playlist.title

            tvMenuNumberOfTracks.text = resources.getQuantityString(
                CASE_OF_TRACK_PLURALS_ID,
                playlist.numberOfTracks,
                playlist.numberOfTracks
            )

            ivMenuPlaylistCover.setCoverImage(playlist.coverPath)
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

    private fun onSharePlaylistClicked(playlist: PlaylistUi) {
        if (playlist.tracks.isNullOrEmpty()) {
            showMessage(getString(SHARE_MESSAGE_STR_ID))
            return
        }

        val contentUri = playlist.coverPath?.getContentUri()

        viewModel.onSharePlaylistClicked(playlist.toStringMassage(), playlist, contentUri)
    }

    private fun String.getContentUri(): Uri {
        val path = File(this)
        val file = File(path.parent, path.name)

        return FileProvider.getUriForFile(
            requireContext(),
            FILE_PROVIDER_AUTHORITY,
            file
        )
    }

    private fun PlaylistUi.toStringMassage(): String {
        return buildString {
            append(title)
            append(NEW_LINE_STRING)
            if (!description.isNullOrEmpty()) append(description + NEW_LINE_STRING)
            append(numberOfTracks.toNumberOfTracksString(requireContext()))
            append(DOUBLE_NEW_LINE_STRING)
            append(tracks?.toStringMessage())
        }
    }

    private fun List<TrackItem>.toStringMessage(): String {
        val preparedTracks =
            this.mapIndexed { index, recyclerTrack ->
                with(recyclerTrack) {
                    String.format(
                        getString(TRACK_STRING_TEMPLATE_ID),
                        (index + 1).toString(),
                        track.artistName,
                        track.trackName,
                        track.trackTimeMillis?.toTimeMMSS()
                    )
                }
            }

        return preparedTracks.joinToString(separator = NEW_LINE_STRING)
    }

    // Устанавливает верхний отступ View, чтобы при появлении Sharesheet разметка
    // не уезжала за статусбаром.
    private fun View.setMarginTop(insets: Insets) {
        this.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            if (insets.top != 0) topMargin =
                requireActivity().window.decorView.rootWindowInsets.getInsets(WindowInsets.Type.systemBars()).top
        }
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.clRoot, message, Snackbar.LENGTH_SHORT).show()
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
        const val PLAYLIST_DIALOG_TITLE = R.string.deletion_of_a_playlist_dialog_title

        const val SHARE_MESSAGE_STR_ID = R.string.tracks_is_not_available_to_share

        const val TRACK_STRING_TEMPLATE_ID = R.string.track_item_template

        const val NEW_LINE_STRING = "\n"
        const val DOUBLE_NEW_LINE_STRING = "\n\n"

        const val GAP_ABOVE_BOTTOM_SHEET_DP = 8

        const val FILE_PROVIDER_AUTHORITY = "com.victor_sml.playlistmaker.fileprovider"
    }
}