package com.victor_sml.playlistmaker.search.ui.stateholders

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.victor_sml.playlistmaker.App
import com.victor_sml.playlistmaker.common.models.Track
import com.victor_sml.playlistmaker.search.domain.api.HistoryInteractor
import com.victor_sml.playlistmaker.search.domain.api.SearchInteractor
import com.victor_sml.playlistmaker.common.models.TrackUi
import com.victor_sml.playlistmaker.search.domain.Resource
import com.victor_sml.playlistmaker.common.utils.Utils.toTimeMMSS
import com.victor_sml.playlistmaker.creator.Creator
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.Loading
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.SearchResult
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.History
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.NothingFound
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.ConnectionFailure
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.Empty
import com.victor_sml.playlistmaker.search.domain.Resource.RequestState.SUCCESS
import com.victor_sml.playlistmaker.search.domain.Resource.RequestState.NOTHING_FOUND
import com.victor_sml.playlistmaker.search.domain.Resource.RequestState.CONNECTION_FAILURE
import com.victor_sml.playlistmaker.common.utils.Utils.toDateYYYY

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val historyInteractor: HistoryInteractor
) : ViewModel() {
    private var screenState = MutableLiveData<SearchScreenState>(Loading)

    fun getScreenState(): LiveData<SearchScreenState> = screenState

    fun searchTracks(searchRequest: String) {
        screenState.postValue(Loading)

        searchInteractor.searchTracks(searchRequest, object : SearchInteractor.SearchResultConsumer {
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun consume(tracks: List<Track>?, requestState: Resource.RequestState) {
                when (requestState) {
                    SUCCESS -> tracks?.let { tracks ->
                        screenState.postValue(SearchResult(tracks.map { mapTrackToUi(it) }))
                    }
                    NOTHING_FOUND -> screenState.postValue(NothingFound)
                    CONNECTION_FAILURE -> screenState.postValue(ConnectionFailure)
                }
            }
        })
    }

    fun addToHistory(track: TrackUi) {
        historyInteractor.addTrack(track)
    }

    fun getHistory() {
        historyInteractor.getHistory(
            object : HistoryInteractor.HistoryConsumer {
                override fun consume(tracks: List<TrackUi>?) {
                    if (tracks.isNullOrEmpty()) screenState.postValue(Empty)
                    else screenState.postValue(History(tracks))
                }
            }
        )
    }

    fun clearHistory() {
        screenState.postValue(Empty)
        historyInteractor.clearHistory()
    }

    private fun mapTrackToUi(track: Track): TrackUi {
        return TrackUi(
            track.artistName,
            track.trackName,
            track.artworkUrl100,
            track.trackTimeMillis?.toTimeMMSS(),
            track.collectionName,
            track.releaseDate?.toDateYYYY(),
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
    }

    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App)
                SearchViewModel(
                    Creator.provideSearchInteractor(context),
                    Creator.provideHistoryInteractor(app)
                )
            }
        }
    }
}