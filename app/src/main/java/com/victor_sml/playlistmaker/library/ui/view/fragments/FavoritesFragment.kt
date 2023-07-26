package com.victor_sml.playlistmaker.library.ui.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.Constants.CLICK_DEBOUNCE_DELAY
import com.victor_sml.playlistmaker.common.Constants.TRACK_FOR_PLAYER
import com.victor_sml.playlistmaker.common.models.Track
import com.victor_sml.playlistmaker.common.ui.BindingFragment
import com.victor_sml.playlistmaker.common.utils.debounce
import com.victor_sml.playlistmaker.databinding.FragmentFavoritesBinding
import com.victor_sml.playlistmaker.library.ui.stateholder.FavoritesViewModel
import com.victor_sml.playlistmaker.main.ui.stateholder.SharedViewModel
import com.victor_sml.playlistmaker.common.utils.recycler.api.RecyclerController
import com.victor_sml.playlistmaker.common.utils.recycler.delegates.MessageDelegate
import com.victor_sml.playlistmaker.common.utils.recycler.delegates.SpaceDelegate
import com.victor_sml.playlistmaker.common.utils.recycler.delegates.TrackDelegate
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FavoritesFragment : BindingFragment<FragmentFavoritesBinding>() {
    private val viewModel by viewModel<FavoritesViewModel>()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var isFavoritesChanged: Boolean = false

    private lateinit var recyclerController: RecyclerController
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private val trackClickListener = object : TrackDelegate.TrackClickListener {
        override fun onTrackClick(track: Track) {
            onTrackClickDebounce(track)
        }
    }

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFavoritesBinding {
        return FragmentFavoritesBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce =
            debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope) { track ->
                sharedViewModel.passTrack(TRACK_FOR_PLAYER, track)
                findNavController().navigate(R.id.action_global_player)
            }

        recyclerController = get(null) {
            parametersOf(
                binding.rwFavoriteTracks,
                arrayListOf(
                    TrackDelegate(trackClickListener),
                    MessageDelegate(),
                    SpaceDelegate()
                )
            )
        }

        viewModel.updateFavorites()

        viewModel.getFavoriteTracks().observe(viewLifecycleOwner) { recyclerItems ->
            binding.rwFavoriteTracks.isVisible = true
            recyclerController.updateContent(recyclerItems)
            isFavoritesChanged = true
        }
    }

    override fun onResume() {
        super.onResume()
        binding.rwFavoriteTracks.isVisible = !isFavoritesChanged
    }

    override fun onPause() {
        super.onPause()
        isFavoritesChanged = false
    }

    companion object {
        fun newInstance(): FavoritesFragment = FavoritesFragment()
    }
}