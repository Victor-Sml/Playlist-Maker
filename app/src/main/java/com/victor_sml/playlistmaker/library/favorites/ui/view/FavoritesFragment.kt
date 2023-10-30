package com.victor_sml.playlistmaker.library.favorites.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.Constants.CLICK_DEBOUNCE_DELAY
import com.victor_sml.playlistmaker.common.Constants.TRACK_FOR_PLAYER
import com.victor_sml.playlistmaker.common.domain.models.Track
import com.victor_sml.playlistmaker.common.ui.BindingFragment
import com.victor_sml.playlistmaker.common.ui.recycler.adapters.RecyclerAdapter
import com.victor_sml.playlistmaker.common.utils.debounce
import com.victor_sml.playlistmaker.databinding.FragmentFavoritesBinding
import com.victor_sml.playlistmaker.library.favorites.ui.stateholder.FavoritesViewModel
import com.victor_sml.playlistmaker.main.ui.stateholder.SharedViewModel
import com.victor_sml.playlistmaker.common.ui.recycler.delegates.MessageDelegate
import com.victor_sml.playlistmaker.common.ui.recycler.delegates.SpaceDelegate
import com.victor_sml.playlistmaker.common.ui.recycler.delegates.TrackDelegate
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : BindingFragment<FragmentFavoritesBinding>() {
    private val viewModel by viewModel<FavoritesViewModel>()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private val trackClickListener = object : TrackDelegate.TrackClickListener {
        override fun onTrackClick(track: Track) {
            onTrackClickDebounce(track)
        }
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentFavoritesBinding {
        return FragmentFavoritesBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce =
            debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope) { track ->
                sharedViewModel.passTrack(TRACK_FOR_PLAYER, track)
                findNavController().navigate(R.id.action_global_player)
            }

        viewModel.updateFavorites()

        viewModel.getFavoriteTracks().observe(viewLifecycleOwner) { recyclerItems ->
            binding.rwFavoriteTracks.isVisible = true
            recyclerAdapter.update(recyclerItems)
        }

        initRecycler()
    }

    private fun initRecycler() {
        recyclerAdapter =
            RecyclerAdapter(
                arrayListOf(
                    TrackDelegate(trackClickListener),
                    MessageDelegate(),
                    SpaceDelegate()
                )
            )
        binding.rwFavoriteTracks.layoutManager = LinearLayoutManager(requireContext())
        binding.rwFavoriteTracks.adapter = recyclerAdapter
    }

    companion object {
        fun newInstance(): FavoritesFragment = FavoritesFragment()
    }
}