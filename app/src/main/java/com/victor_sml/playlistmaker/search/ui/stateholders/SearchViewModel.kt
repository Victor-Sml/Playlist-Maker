package com.victor_sml.playlistmaker.search.ui.stateholders

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.stringProvider.domain.api.StringInteractor
import com.victor_sml.playlistmaker.search.domain.api.HistoryInteractor
import com.victor_sml.playlistmaker.search.domain.api.SearchInteractor
import com.victor_sml.playlistmaker.common.models.Track
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
import com.victor_sml.playlistmaker.search.ui.view.recycler.api.RecyclerItem.TrackItem

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val historyInteractor: HistoryInteractor,
    private val stringInteractor: StringInteractor
) : ViewModel() {
    private var screenState = MutableLiveData<SearchScreenState>(Loading)
    private val getString: (stringId: Int) -> String = { stringInteractor.getString(it) }

    fun getScreenState(): LiveData<SearchScreenState> = screenState

    fun searchTracks(searchRequest: String) {
        screenState.postValue(Loading)

        searchInteractor.searchTracks(
            searchRequest,
            object : SearchInteractor.SearchResultConsumer {
                override fun consume(tracks: List<Track>?, requestState: ResponseState) {
                    when (requestState) {
                        SUCCESS -> tracks?.let { postSuccessState(tracks) }
                        NOTHING_FOUND -> postNothingFoundState()
                        CONNECTION_FAILURE -> postConnectionFailureState { searchTracks(searchRequest) }
                    }
                }
            })
    }

    fun getHistory() {
        screenState.postValue(Loading)

        historyInteractor.getHistory(
            object : HistoryInteractor.HistoryConsumer {
                override fun consume(tracks: List<Track>?, requestState: ResponseState) {
                    when (requestState) {
                        SUCCESS -> tracks?.let { postHistoryState(tracks) }

                        NOTHING_FOUND -> screenState.postValue(Empty)

                        CONNECTION_FAILURE -> {
                            postConnectionFailureState {
                                screenState.postValue(Loading)
                                historyInteractor.restoreHistory(this)
                            }
                        }
                    }
                }
            }
        )
    }

    fun addToHistory(track: Track) {
        historyInteractor.addTrack(track)
    }

    private fun clearHistory() {
        screenState.postValue(Empty)
        historyInteractor.clearHistory()
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
            ConnectionFailure(
                getConnectionFailureItems { callback() }
            )
        )
    }

    private fun getSearchResultItems(tracks: List<Track>) = getTrackItems(tracks)

    private fun getHistoryItems(tracks: List<Track>) =
        (arrayListOf<RecyclerItem>() +
                Header(getString(SEARCH_HISTORY_STR)) +
                getTrackItems(tracks) +
                Button(getString(CLEAR_HISTORY_STR)) { clearHistory() }) as ArrayList<RecyclerItem>

    private fun getNothingFoundItems() =
        arrayListOf<RecyclerItem>(
            Message(UPSET_EMOJI_DRAWABLE, getString(NOTHING_FOUND_STR))
        )

    private fun getConnectionFailureItems(callback: () -> Unit) =
        arrayListOf(
            Message(CONNECTION_FAILURE_DRAWABLE, getString(CONNECTION_FAILURE_STR)),
            Button(getString(REFRESH_STR), callback)
        )

    private fun getTrackItems(tracks: List<Track>) =
        ArrayList<RecyclerItem>(tracks.map { TrackItem(it) })


    companion object {
        const val SEARCH_HISTORY_STR = R.string.search_history
        const val CLEAR_HISTORY_STR = R.string.clear_history
        const val NOTHING_FOUND_STR = R.string.nothing_found
        const val CONNECTION_FAILURE_STR = R.string.connection_failure
        const val REFRESH_STR = R.string.refresh

        const val UPSET_EMOJI_DRAWABLE = R.drawable.ic_nothing_found
        const val CONNECTION_FAILURE_DRAWABLE = R.drawable.ic_connection_failure
    }
}