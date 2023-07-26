package com.victor_sml.playlistmaker.library.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.ui.BindingFragment
import com.victor_sml.playlistmaker.databinding.FragmentLibraryBinding
import com.victor_sml.playlistmaker.library.ui.stateholder.FavoritesViewModel
import com.victor_sml.playlistmaker.library.ui.stateholder.PlaylistViewModel
import com.victor_sml.playlistmaker.library.ui.view.fragments.ViewPagerAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryFragment : BindingFragment<FragmentLibraryBinding>() {
    private var tabLayoutMediator: TabLayoutMediator? = null
    private val favoritesViewModel by viewModel<FavoritesViewModel>()
    private val playlistViewModel by viewModel<PlaylistViewModel>()

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLibraryBinding {
        return FragmentLibraryBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewPager.adapter = ViewPagerAdapter(childFragmentManager, lifecycle)

        tabLayoutMediator =
            TabLayoutMediator(binding.tablayout, binding.viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.favorite_tracks)
                    1 -> tab.text = getString(R.string.playlists)
                }
            }
        tabLayoutMediator?.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator?.detach()
    }

    companion object {
        fun newInstance(): LibraryFragment = LibraryFragment()
    }
}