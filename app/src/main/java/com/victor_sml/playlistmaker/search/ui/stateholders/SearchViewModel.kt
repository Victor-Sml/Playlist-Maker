package com.victor_sml.playlistmaker.search.ui.stateholders

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.domain.GetStringUseCase
import com.victor_sml.playlistmaker.search.domain.api.HistoryInteractor
import com.victor_sml.playlistmaker.search.domain.api.SearchInteractor
import com.victor_sml.playlistmaker.common.domain.models.Track
import com.victor_sml.playlistmaker.common.utils.Resource
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.Loading
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.SearchResult
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.History
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.NothingFound
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.ConnectionFailure
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.Empty
import com.victor_sml.playlistmaker.common.utils.Resource.ResponseState
import com.victor_sml.playlistmaker.common.ui.recycler.RecyclerItemsBuilder
import com.victor_sml.playlistmaker.common.utils.Resource.ErrorState.CONNECTION_FAILURE
import com.victor_sml.playlistmaker.common.utils.Resource.ErrorState.NOTHING_FOUND
import com.victor_sml.playlistmaker.common.utils.Resource.State.EMPTY
import com.victor_sml.playlistmaker.common.utils.Resource.State.SUCCESS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val historyInteractor: HistoryInteractor,
    private val getStringUseCase: GetStringUseCase,
) : ViewModel() {
    var isFragmentDestroyed = false

    private val recyclerBuilder: RecyclerItemsBuilder by inject(RecyclerItemsBuilder::class.java)
    private val cache = SearchCache()
    private var viewRequireHistory = false

    private var screenState = MutableLiveData<SearchScreenState>(Loading)

    fun getScreenState(): LiveData<SearchScreenState> = screenState

    fun searchTracks(searchRequest: String) {
        val (tracks, state) = cache.getResponse(searchRequest)

        if (searchRequest == cache.getRequest() && state in setOf(SUCCESS, NOTHING_FOUND, EMPTY)) {
            processSearchResponse(searchRequest, tracks, state)
            return
        }

        viewRequireHistory = false
        screenState.postValue(Loading)
        requestSearchResult(searchRequest)
    }

    fun getHistory() {
        if (screenState.value is History) return

        viewRequireHistory = true
        screenState.postValue(Loading)
        requestHistory()
    }

    fun onTrackClicked(track: Track) {
        if (screenState.value is History) return

        addToHistory(track)
    }

    fun addToHistory(track: Track) {
        viewModelScope.launch { historyInteractor.addTrack(track) }
    }

    private fun requestSearchResult(searchRequest: String) {
        viewModelScope.launch(Dispatchers.IO) {
            searchInteractor.searchTracks(searchRequest).collect { (tracks, state) ->
                if (viewRequireHistory) return@collect
                processSearchResponse(searchRequest, tracks, state)
            }
        }
    }

    private fun requestHistory() {
        viewModelScope.launch {
            historyInteractor
                .getHistory()
                .let { processHistoryResponse(it) }
        }
    }

    private fun processSearchResponse(
        searchRequest: String,
        tracks: List<Track>?,
        state: ResponseState,
    ) {
        when (state) {
            SUCCESS -> tracks?.let { tracks ->
                postSuccessState(tracks)
                cache.fillCache(searchRequest, Resource.Success(tracks))
            }

            NOTHING_FOUND -> {
                postNothingFoundState()
                cache.fillCache(searchRequest, Resource.Error(NOTHING_FOUND))
            }

            CONNECTION_FAILURE -> {
                postConnectionFailureState { searchTracks(searchRequest) }
                cache.fillCache(searchRequest, Resource.Error(CONNECTION_FAILURE))
            }
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
            CONNECTION_FAILURE -> {
                postConnectionFailureState { connectionFailureCallback() }
            }
        }
    }

    private fun clearHistory() {
        screenState.postValue(Empty)
        viewModelScope.launch { historyInteractor.clearHistory() }
    }

    private fun postSuccessState(tracks: List<Track>) {
        recyclerBuilder
            .addTracks(tracks)
            .getItems()
            .let { recyclerItems ->
                screenState.postValue(SearchResult(recyclerItems))
            }
    }

    private fun postHistoryState(tracks: List<Track>) {
        recyclerBuilder
            .addHeader(getStringUseCase.execute(SEARCH_HISTORY_STR_ID))
            .addTracks(tracks)
            .addButton(getStringUseCase.execute(CLEAR_HISTORY_STR_ID)) { clearHistory() }
            .getItems()
            .let { recyclerItems ->
                screenState.postValue(History(recyclerItems))
            }
    }

    private fun postNothingFoundState() {
        recyclerBuilder
            .addSpace(MESSAGE_MARGIN_TOP_DP)
            .addMessage(NOTHING_FOUND_DRAWABLE_ID, getStringUseCase.execute(NOTHING_FOUND_STR_ID))
            .addSpace(MESSAGE_FOUND_MARGIN_BOTTOM_DP)
            .getItems()
            .let { recyclerItems -> screenState.postValue(NothingFound(recyclerItems)) }
    }

    private fun postConnectionFailureState(callback: () -> Unit) {
        recyclerBuilder
            .addSpace(MESSAGE_MARGIN_TOP_DP)
            .addMessage(
                CONNECTION_FAILURE_DRAWABLE_ID,
                getStringUseCase.execute(CONNECTION_FAILURE_STR_ID)
            )
            .addButton(getStringUseCase.execute(REFRESH_STR_ID), callback)
            .getItems()
            .let { recyclerItems -> screenState.postValue(ConnectionFailure(recyclerItems)) }
    }

    private inner class SearchCache {
        private var latestRequest = ""
        private var latestResponse: Resource<List<Track>> = Resource.Empty()

        fun fillCache(searchRequest: String, searchResponse: Resource<List<Track>>) {
            latestRequest = searchRequest
            latestResponse = searchResponse
        }

        fun getRequest() = latestRequest

        fun getResponse(searchRequest: String): Resource<List<Track>> {
            return if (searchRequest == latestRequest) latestResponse
            else Resource.Empty()
        }
    }

    companion object {
        const val SEARCH_HISTORY_STR_ID = "search_history"
        const val CLEAR_HISTORY_STR_ID = "clear_history"
        const val NOTHING_FOUND_STR_ID = "nothing_found"
        const val CONNECTION_FAILURE_STR_ID = "connection_failure"
        const val REFRESH_STR_ID = "refresh"

        const val NOTHING_FOUND_DRAWABLE_ID = R.drawable.ic_nothing_found
        const val CONNECTION_FAILURE_DRAWABLE_ID = R.drawable.ic_connection_failure

        const val MESSAGE_MARGIN_TOP_DP = 106
        const val MESSAGE_FOUND_MARGIN_BOTTOM_DP = 24
    }
}