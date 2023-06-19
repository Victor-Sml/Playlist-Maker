package com.victor_sml.playlistmaker.library.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.databinding.FragmentLibraryBinding
import com.victor_sml.playlistmaker.library.ui.stateholder.FavoritesViewModel
import com.victor_sml.playlistmaker.library.ui.stateholder.PlaylistViewModel
import com.victor_sml.playlistmaker.library.ui.view.fragments.ViewPagerAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!
    private var tabLayoutMediator: TabLayoutMediator? = null
    private val favoritesViewModel by viewModel<FavoritesViewModel>()
    private val playlistViewModel by viewModel<PlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator?.detach()
    }

    companion object {
        fun newInstance(): LibraryFragment = LibraryFragment()
    }
}