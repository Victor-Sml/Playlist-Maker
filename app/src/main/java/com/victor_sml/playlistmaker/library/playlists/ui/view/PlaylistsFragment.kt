package com.victor_sml.playlistmaker.library.playlists.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.domain.models.Playlist
import com.victor_sml.playlistmaker.common.ui.BindingFragment
import com.victor_sml.playlistmaker.common.ui.recycler.adapters.RecyclerAdapter
import com.victor_sml.playlistmaker.common.ui.recycler.api.RecyclerItem
import com.victor_sml.playlistmaker.common.ui.recycler.delegates.PlaylistDelegate
import com.victor_sml.playlistmaker.common.ui.recycler.delegates.PlaylistDelegate.PlaylistClickListener
import com.victor_sml.playlistmaker.databinding.FragmentPlaylistsBinding
import com.victor_sml.playlistmaker.library.playlists.ui.stateholder.PlaylistsScreenState
import com.victor_sml.playlistmaker.library.playlists.ui.stateholder.PlaylistsScreenState.Content
import com.victor_sml.playlistmaker.library.playlists.ui.stateholder.PlaylistsScreenState.Empty
import com.victor_sml.playlistmaker.library.playlists.ui.stateholder.PlaylistsViewModel
import com.victor_sml.playlistmaker.library.ui.view.LibraryFragmentDirections
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : BindingFragment<FragmentPlaylistsBinding>() {
    private val viewModel by viewModel<PlaylistsViewModel>()

    private lateinit var recyclerAdapter: RecyclerAdapter

    private val playlistClickListener = object : PlaylistClickListener {
        override fun onPlaylistClick(playlist: Playlist) {
            val action = LibraryFragmentDirections.actionPlaylistDetails(playlist.id)
            findNavController().navigate(action)
        }
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentPlaylistsBinding {
        return FragmentPlaylistsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeScreenState()
        initializeUI()
    }

    private fun initializeUI() {
        initRecycler()
        setListeners()
    }

    private fun observeScreenState() {
        viewModel.getPlaylistState().observe(viewLifecycleOwner) { state ->
            renderViews(state)
        }
    }

    private fun initRecycler() {
        recyclerAdapter = RecyclerAdapter(arrayListOf(PlaylistDelegate(playlistClickListener)))
        binding.rwPlaylists.layoutManager =
            GridLayoutManager(requireContext(), GRID_LAYOUT_SPAN_COUNT)
        binding.rwPlaylists.adapter = recyclerAdapter
    }

    private fun setListeners() {
        binding.btnRecyclerButton.setOnClickListener {
            findNavController().navigate(R.id.action_global_playlist_editor)
        }
    }

    private fun renderViews(screenState: PlaylistsScreenState) {
        when (screenState) {
            is Empty -> showEmptyMessage()
            is Content -> showPlaylists(screenState.playlists)
        }
    }

    private fun showPlaylists(playlists: ArrayList<RecyclerItem>?) {
        recyclerAdapter.update(playlists)
        showRecycler()
    }

    private fun showEmptyMessage() {
        binding.rwPlaylists.isVisible = false
        binding.tvNotPlaylists.isVisible = true
    }

    private fun showRecycler() {
        binding.tvNotPlaylists.isVisible = false
        binding.rwPlaylists.isVisible = true
    }

    companion object {
        fun newInstance() = PlaylistsFragment()

        const val GRID_LAYOUT_SPAN_COUNT = 2
    }
}