package com.victor_sml.playlistmaker

import android.content.SharedPreferences
import android.view.View
import android.view.View.VISIBLE
import android.view.View.GONE
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class TracksHandler(
    val recyclerView: RecyclerView,
    val nothingFound: View,
    val connectionFailure: View,
    private val historyTitle: TextView,
) {

    private val app = recyclerView.context.applicationContext as App
    private val adapterDelegates = arrayListOf(
        TrackDelegate(app, this),
        ClearButtonDelegate(this)
    )
    private val adapter = TrackAdapter(adapterDelegates)
    private val searchHistory = SearchHistory(app.getSharedPreferences())
    private val viewContainer = ViewContainer(recyclerView, nothingFound, connectionFailure)

    init {
        initRecycler()
        restoreTracks()
    }

    private fun initRecycler() {
        viewContainer.recyclerView.layoutManager =
            LinearLayoutManager(app.applicationContext, LinearLayoutManager.VERTICAL, false)
        viewContainer.recyclerView.adapter = adapter
    }

    private fun restoreTracks() {
        if (lookedTracks.isEmpty()) searchHistory.getSavedTracks()?.toCollection(lookedTracks)
    }

    fun showSearchResult(tracks: ArrayList<Track>) {
        viewContainer.setVisibility(RequestState.FOUND)
        adapter.update(tracks, false)
    }

    fun showSearchResult(requestState: RequestState) {
        viewContainer.setVisibility(requestState)
    }

    fun clearSearchResult() {
        if (!showHistory()) {
            adapter.update()
            viewContainer.clearVisibility()
        }
    }

    fun saveTrack(track: Track) {
        searchHistory.putTrack(track)
    }

    fun showHistory(): Boolean {
        if (lookedTracks.isNotEmpty()) {
            adapter.update(lookedTracks, true)
            viewContainer.setVisibility(RequestState.HISTORY)
            return true
        } else
            return false
    }

    fun clearHistory() {
        lookedTracks.clear()
        searchHistory.clearHistory()
        adapter.update()
        historyTitle.visibility = GONE
    }

    /**
     * Содержит View сообщений-заглушек: [nothingFoundView] - "Ничего не нашлось",
     * [connectionFailureView] - "Проблемы со связью", [recyclerView] - RecyclerView с треклистом и
     * управляет отображением их на экране.
     */
    inner class ViewContainer(
        val recyclerView: RecyclerView,
        val nothingFoundView: View,
        val connectionFailureView: View
    ) {
        /**
         * В соответствии с [RequestState] переданном в [requestState] устанавливает значение visibility
         * для [recyclerView], [nothingFoundView], [connectionFailureView], [historyTitle].
         */
        fun setVisibility(requestState: RequestState) {
            recyclerView.visibility = requestState.recyclerVisibility
            nothingFoundView.visibility = requestState.nothingFoundViewVisibility
            connectionFailureView.visibility = requestState.connectionFailureViewVisibility
            historyTitle.visibility = requestState.historyTitleVisibility
        }

        fun clearVisibility() {
            recyclerView.visibility = GONE
            nothingFoundView.visibility = GONE
            connectionFailureView.visibility = GONE
            historyTitle.visibility = GONE
        }
    }

    /**
     * Статус поискового запроса.
     * Каждый экземпляр сожержит значение visibility для сообщений-заглушек и RecyclerView:
     *  [ViewContainer.recyclerView] = [recyclerVisibility],
     *  [ViewContainer.nothingFoundView] = [nothingFoundViewVisibility],
     *  [ViewContainer.connectionFailureView] = [connectionFailureViewVisibility].
     */
    enum class RequestState(
        val recyclerVisibility: Int,
        val nothingFoundViewVisibility: Int,
        val connectionFailureViewVisibility: Int,
        val historyTitleVisibility: Int
    ) {
        FOUND(VISIBLE, GONE, GONE, GONE),
        NOTHING_FOUND(GONE, VISIBLE, GONE, GONE),
        CONNECTION_FAILURE(GONE, GONE, VISIBLE, GONE),
        HISTORY(VISIBLE,GONE, GONE, VISIBLE)
    }

    private inner class SearchHistory(private val sharedPreferences: SharedPreferences) {

        fun putTrack(track: Track) {
            updateHistory(track)
            sharedPreferences.edit()
                .putString(LOOKED_TRACKS, Gson().toJson(lookedTracks))
                .apply()
        }

        private fun updateHistory(track: Track) {
            if (lookedTracks.isNotEmpty()) {
                if (track != lookedTracks[0]) lookedTracks.remove(track) else return
            }

            if (lookedTracks.size == HISTORY_SIZE) lookedTracks.removeLast()
            lookedTracks.add(0, track)
        }


        fun getSavedTracks(): Array<Track>? =
            Gson().fromJson(
                sharedPreferences.getString(LOOKED_TRACKS, null),
                Array<Track>::class.java
            )

        fun clearHistory() {
            lookedTracks.clear()
            sharedPreferences.edit()
                .remove(LOOKED_TRACKS)
                .apply()
        }
    }

    companion object {
        /**
         * Ключ для доступа к списку просмотренных треков в [SearchHistory.sharedPreferences].
         */
        private const val LOOKED_TRACKS = "looked tracks"

        /**
         * Максимальное количество треков хранящихся в истории.
         */
        private const val HISTORY_SIZE = 10

        /**
         *  Список просмотренных треков.
         */
        private val lookedTracks: ArrayList<Track> = arrayListOf()
    }
}