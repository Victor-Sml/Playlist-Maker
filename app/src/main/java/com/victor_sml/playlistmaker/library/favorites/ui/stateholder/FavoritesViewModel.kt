package com.victor_sml.playlistmaker.library.favorites.ui.stateholder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victor_sml.playlistmaker.common.Constants.NOTHING_FOUND_DRAWABLE_ID
import com.victor_sml.playlistmaker.common.domain.GetStringUseCase
import com.victor_sml.playlistmaker.common.domain.api.TracksInteractor
import com.victor_sml.playlistmaker.common.models.Track
import com.victor_sml.playlistmaker.common.utils.recycler.RecyclerItemsBuilder
import com.victor_sml.playlistmaker.common.utils.recycler.api.RecyclerItem
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val libraryInteractor: TracksInteractor,
    private val getStringUseCase: GetStringUseCase,
) : ViewModel() {
    private val recyclerBuilder = RecyclerItemsBuilder()
    private val favoriteTracks = MutableLiveData<ArrayList<RecyclerItem>>(null)

    init {
        updateFavorites()
    }

    fun getFavoriteTracks(): LiveData<ArrayList<RecyclerItem>> = favoriteTracks

    fun updateFavorites() {
        viewModelScope.launch {
            libraryInteractor.getFavoriteTracks().collect { tracks ->
                if (tracks.isNotEmpty()) postFavorites(tracks)
                else postEmptyMessage()
            }
        }
    }

    private fun postFavorites(tracks: List<Track>) {
        recyclerBuilder
            .addTracks(tracks)
            .getItems()
            .let { favoriteTracks.postValue(it) }
    }

    private fun postEmptyMessage() {
        recyclerBuilder
            .addSpace(102)
            .addMessage(NOTHING_FOUND_DRAWABLE_ID,
                getStringUseCase.execute(EMPTY_LIBRARY_STR_ID))
            .addSpace(BOTTOM_SPACE_DP)
            .getItems()
            .let { favoriteTracks.postValue(it) }
    }

    companion object {
        const val EMPTY_LIBRARY_STR_ID = "empty_library"

        const val BOTTOM_SPACE_DP = 24
    }
}