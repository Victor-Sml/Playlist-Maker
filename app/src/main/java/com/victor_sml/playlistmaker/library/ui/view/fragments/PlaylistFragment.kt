package com.victor_sml.playlistmaker.library.ui.view.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import com.victor_sml.playlistmaker.common.ui.BindingFragment
import com.victor_sml.playlistmaker.databinding.FragmentPlaylistsBinding
import com.victor_sml.playlistmaker.library.ui.stateholder.PlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : BindingFragment<FragmentPlaylistsBinding>() {
    private val viewModel by viewModel<PlaylistViewModel>()

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPlaylistsBinding {
        return FragmentPlaylistsBinding.inflate(inflater, container, false)
    }

    companion object {
        fun newInstance() = PlaylistFragment()
    }
}