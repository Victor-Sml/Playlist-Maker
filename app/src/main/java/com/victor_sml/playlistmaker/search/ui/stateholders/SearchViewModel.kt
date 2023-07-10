package com.victor_sml.playlistmaker.search.ui.stateholders

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.domain.GetStringUseCase
import com.victor_sml.playlistmaker.search.domain.api.HistoryInteractor
import com.victor_sml.playlistmaker.search.domain.api.SearchInteractor
import com.victor_sml.playlistmaker.common.models.Track
import com.victor_sml.playlistmaker.common.utils.Resource
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.Loading
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.SearchResult
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.History
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.NothingFound
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.ConnectionFailure
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.Empty
import com.victor_sml.playlistmaker.search.ui.view.recycler.api.RecyclerItem
import com.victor_sml.playlistmaker.common.utils.Resource.ResponseState
import com.victor_sml.playlistmaker.common.utils.Resource.ResponseState.CONNECTION_FAILURE
import com.victor_sml.playlistmaker.common.utils.Resource.ResponseState.NOTHING_FOUND
import com.victor_sml.playlistmaker.common.utils.Resource.ResponseState.SUCCESS
import com.victor_sml.playlistmaker.search.ui.view.recycler.api.RecyclerItem.Button
import com.victor_sml.playlistmaker.search.ui.view.recycler.api.RecyclerItem.Header
import com.victor_sml.playlistmaker.search.ui.view.recycler.api.RecyclerItem.Message
import com.victor_sml.playlistmaker.search.ui.view.recycler.api.RecyclerItem.Space
import com.victor_sml.playlistmaker.search.ui.view.recycler.api.RecyclerItem.TrackItem
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val historyInteractor: HistoryInteractor,
    private val getStringUseCase: GetStringUseCase,
) : ViewModel() {
    private var screenState = MutableLiveData<SearchScreenState>(Loading)
    private var lastSearchRequest = ""
    private var viewRequireHistory = false

    fun getScreenState(): LiveData<SearchScreenState> = screenState

    fun searchTracks(searchRequest: String) {
        if (searchRequest == lastSearchRequest) return

        viewRequireHistory = false
        screenState.postValue(Loading)

        viewModelScope.launch {
            searchInteractor.searchTracks(searchRequest).collect { (tracks, state) ->
                if (viewRequireHistory) return@collect
                processSearchResponse(searchRequest, tracks, state)
            }
        }
    }

    fun getHistory() {
        viewRequireHistory = true
        screenState.postValue(Loading)

        viewModelScope.launch {
            historyInteractor
                .getHistory()
                .let { processHistoryResponse(it) }
        }
    }

    fun addToHistory(track: Track) {
        viewModelScope.launch { historyInteractor.addTrack(track) }
    }

    private fun processSearchResponse(
        searchRequest: String,
        tracks: List<Track>?,
        state: ResponseState,
    ) {
        when (state) {
            SUCCESS -> tracks?.let {
                postSuccessState(it)
                lastSearchRequest = searchRequest
            }
            NOTHING_FOUND -> postNothingFoundState()
            CONNECTION_FAILURE -> postConnectionFailureState { searchTracks(searchRequest) }
        }
    }

    private fun processHistoryResponse(response: Resource<List<Track>>) {
        val (tracks, state) = response

        val connectionFailureCallback = {
            screenState.postValue(Loading)
            getHistory()
        }

        when (state) {
            SUCCESS -> tracks?.let { postHistoryState(tracks) }
            NOTHING_FOUND -> screenState.postValue(Empty)
            CONNECTION_FAILURE -> { postConnectionFailureState { connectionFailureCallback() }
            }
        }
    }

    private fun clearHistory() {
        screenState.postValue(Empty)
        viewModelScope.launch { historyInteractor.clearHistory() }
    }

    private fun postSuccessState(tracks: List<Track>) {
        val recyclerItems = getSearchResultItems(tracks)
        screenState.postValue(SearchResult(recyclerItems))
    }

    private fun postHistoryState(tracks: List<Track>) {
        val recyclerItems = getHistoryItems(tracks)
        screenState.postValue(History(recyclerItems))
    }

    private fun postNothingFoundState() {
        screenState.postValue(NothingFound(getNothingFoundItems()))
    }

    private fun postConnectionFailureState(callback: () -> Unit) {
        screenState.postValue(
            ConnectionFailure(getConnectionFailureItems { callback() })
        )
    }

    private fun getSearchResultItems(tracks: List<Track>) = getTrackItems(tracks)

    private fun getHistoryItems(tracks: List<Track>) =
        (arrayListOf<RecyclerItem>() +
                Header(getStringUseCase.execute(SEARCH_HISTORY_ID)) +
                getTrackItems(tracks) +
                Button(getStringUseCase.execute(CLEAR_HISTORY_ID)) { clearHistory() }) as ArrayList<RecyclerItem>

    private fun getNothingFoundItems() =
        arrayListOf(
            Message(NOTHING_FOUND_DRAWABLE, getStringUseCase.execute(NOTHING_FOUND_ID)),
            Space(NOTHING_FOUND_BOTTOM_SPACE_DP)
        )

    private fun getConnectionFailureItems(callback: () -> Unit) =
        arrayListOf(
            Message(CONNECTION_FAILURE_DRAWABLE, getStringUseCase.execute(CONNECTION_FAILURE_ID)),
            Button(getStringUseCase.execute(REFRESH_ID), callback)
        )

    private fun getTrackItems(tracks: List<Track>) =
        ArrayList<RecyclerItem>(tracks.map { TrackItem(it) })


    companion object {
        const val SEARCH_HISTORY_ID = "search_history"
        const val CLEAR_HISTORY_ID = "clear_history"
        const val NOTHING_FOUND_ID = "nothing_found"
        const val CONNECTION_FAILURE_ID = "connection_failure"
        const val REFRESH_ID = "refresh"

        const val NOTHING_FOUND_DRAWABLE = R.drawable.ic_nothing_found
        const val CONNECTION_FAILURE_DRAWABLE = R.drawable.ic_connection_failure

        const val NOTHING_FOUND_BOTTOM_SPACE_DP = 24
    }
}