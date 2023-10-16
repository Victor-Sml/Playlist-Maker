package com.victor_sml.playlistmaker.library.playlists.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.models.Playlist
import com.victor_sml.playlistmaker.common.ui.BindingFragment
import com.victor_sml.playlistmaker.databinding.FragmentPlaylistsBinding
import com.victor_sml.playlistmaker.library.playlists.ui.stateholder.PlaylistScreenState
import com.victor_sml.playlistmaker.library.playlists.ui.stateholder.PlaylistScreenState.Content
import com.victor_sml.playlistmaker.library.playlists.ui.stateholder.PlaylistScreenState.Empty
import com.victor_sml.playlistmaker.library.playlists.ui.stateholder.PlaylistViewModel
import com.victor_sml.playlistmaker.common.utils.recycler.adapters.UltimatePlaylistAdapter
import com.victor_sml.playlistmaker.common.utils.recycler.adapters.UltimatePlaylistAdapter.PlaylistClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : BindingFragment<FragmentPlaylistsBinding>() {
    private val viewModel by viewModel<PlaylistViewModel>()

    private lateinit var recyclerAdapter: UltimatePlaylistAdapter

    private val playlistClickListener = object : PlaylistClickListener {
        override fun onPlaylistClick(playlist: Playlist) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentPlaylistsBinding {
        return FragmentPlaylistsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycler()
        setListeners()

        viewModel.getPlaylistState().observe(viewLifecycleOwner) { state ->
            renderViews(state)
        }
    }

    private fun renderViews(screenState: PlaylistScreenState) {
        when (screenState) {
            is Empty -> showEmptyMessage()
            is Content -> showPlaylists(screenState.playlists)
        }
    }

    private fun showPlaylists(playlists: ArrayList<Playlist>?) {
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

    private fun initRecycler() {
        val recycler = binding.rwPlaylists
        recycler.layoutManager = GridLayoutManager(requireContext(), GRID_LAYOUT_SPAN_COUNT)
        recyclerAdapter = UltimatePlaylistAdapter(playlistClickListener)
        recycler.adapter = recyclerAdapter
    }

    private fun setListeners() {
        binding.btnRecyclerButton.setOnClickListener {
            findNavController().navigate(R.id.action_global_new_playlist)
        }
    }

    companion object {
        fun newInstance() = PlaylistFragment()

        const val GRID_LAYOUT_SPAN_COUNT = 2
    }
}