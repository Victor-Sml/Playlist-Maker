package com.victor_sml.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.VISIBLE
import android.view.View.GONE
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.victor_sml.playlistmaker.recycler.ClearButtonDelegate
import com.victor_sml.playlistmaker.recycler.TrackAdapter
import com.victor_sml.playlistmaker.recycler.TrackDelegate
import java.util.EnumMap
import kotlin.collections.ArrayList

const val TRACK_FOR_PLAYER = "track for player"

class TracksHandler(
    private val recyclerView: RecyclerView,
    nothingFound: View,
    connectionFailure: View,
    progressBar: ProgressBar,
    historyTitle: TextView,
) {
    /**
     * Хранит true если [recyclerView] отображает историю просмотренных треков.
     */
    private var isHistory = false
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val app = recyclerView.context.applicationContext as App
    private val searchHistory = SearchHistory(app.getSharedPreferences())
    private val viewContainer = ViewContainer(
        recyclerView,
        nothingFound,
        connectionFailure,
        progressBar,
        historyTitle
    )

    private val trackClickListener = object : TrackDelegate.TrackClickListener {
        override fun onTrackClick(handler: TracksHandler, track: Track) {
            if (clickDebounce()) {
                handler.saveTrack(track)
                val context = handler.recyclerView.context
                Intent(context, PlayerActivity::class.java)
                    .putExtra(TRACK_FOR_PLAYER, track).let { context.startActivity(it) }
            }
        }
    }

    private val clearButtonClickListener = object : ClearButtonDelegate.ClearButtonClickListener {
        override fun onButtonClick(handler: TracksHandler) {
            handler.clearHistory()
        }
    }

    private val adapter = TrackAdapter(
        delegates = arrayListOf(
            TrackDelegate(this, trackClickListener),
            ClearButtonDelegate(this, clearButtonClickListener)
        )
    )

    private fun initRecycler() {
        recyclerView.layoutManager =
            LinearLayoutManager(app.applicationContext, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
    }

    private fun restoreTracks() {
        if (lookedTracks.isEmpty()) searchHistory.getSavedTracks()?.toCollection(lookedTracks)
    }

    init {
        initRecycler()
        restoreTracks()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    /**
     * Выводит на экран переданный в параметр [tracks] - список найденных треков.
     */
    fun showSearchResult(tracks: ArrayList<Track>) {
        viewContainer.setVisibility(RequestState.FOUND)
        adapter.update(tracks, false)
        isHistory = false
    }

    /**
     * Выводит на экран плейсхолдер в соответствии с переданным в [requestState] статусом поиского
     * запроса.
     */
    fun showPlaceholder(requestState: RequestState) {
        viewContainer.setVisibility(requestState)
        isHistory = false
    }

    /**
     * Убирает с экрана список найденных треков и если [lookedTracks] не пуст выводит его на экран.
     */
    fun clearSearchResult() {
        if (showHistory()) return
        adapter.update()
        viewContainer.clearVisibility()
    }

    /**
     * Добавляет [track] в [lookedTracks].
     */
    fun saveTrack(track: Track) {
        searchHistory.updateHistory(track)
    }

    /**
     * Сохраняет [lookedTracks] в [App.sharedPreferences].
     * Если в момент вызова метода, [isHistory] = true, т.е. пользователь переходит на экран плеера
     * по клику на трек из истории просмотров, то для того чтобы при возвращении на [SearchActivity]
     * по нажатию кнопки назад история была обновлена [lookedTracks] записывается в [TrackAdapter.items].
     */
    fun saveHistory() {
        searchHistory.putTracks()
        if (isHistory) adapter.update(lookedTracks, isHistory)
    }

    /**
     * Если [lookedTracks] не пуст, то выводит на экран историю просмотров и возвращает true.
     */
    fun showHistory(): Boolean {
        if (lookedTracks.isNotEmpty()) {
            isHistory = true
            adapter.update(lookedTracks, isHistory)
            viewContainer.setVisibility(RequestState.HISTORY)
            return true
        } else
            return false
    }

    /**
     * Очищает историю просмотров.
     */
    fun clearHistory() {
        searchHistory.clearHistory()
        adapter.update()
        viewContainer.clearVisibility()
        isHistory = false
    }

    private inner class ViewContainer(
        private val recycler: RecyclerView,
        nothingFound: View,
        connectionFailure: View,
        progressBar: ProgressBar,
        history: TextView
    ) {
        private val views: EnumMap<RequestState, View> = EnumMap(RequestState::class.java)
        private var currentVisibleView: View? = null

        init {
            views.apply {
                put(RequestState.FOUND, recycler)
                put(RequestState.NOTHING_FOUND, nothingFound)
                put(RequestState.CONNECTION_FAILURE, connectionFailure)
                put(RequestState.REQUESTING, progressBar)
                put(RequestState.HISTORY, history)
            }
        }

        fun setVisibility(state: RequestState) {
            if (isHistory) {
                recycler.visibility = GONE
            }

            currentVisibleView?.visibility = GONE
            currentVisibleView = views[state]
            currentVisibleView?.visibility = VISIBLE

            if (state == RequestState.HISTORY) {
                recycler.visibility = VISIBLE
            }
        }


        fun clearVisibility() {
            currentVisibleView?.visibility = GONE
            currentVisibleView = null
        }
    }

    enum class RequestState {
        FOUND,
        NOTHING_FOUND,
        CONNECTION_FAILURE,
        REQUESTING,
        HISTORY,
    }

    private inner class SearchHistory(private val sharedPreferences: SharedPreferences) {
        /**
         * Сохраняет [lookedTracks] в [App.sharedPreferences].
         */
        fun putTracks() {
            sharedPreferences.edit()
                .putString(LOOKED_TRACKS, Gson().toJson(lookedTracks))
                .apply()
        }

        /**
         * Добавляет [track] в [lookedTracks].
         */
        fun updateHistory(track: Track) {
            if (lookedTracks.isNotEmpty()) {
                if (track != lookedTracks[0]) lookedTracks.remove(track) else return
            }

            if (lookedTracks.size == HISTORY_SIZE) lookedTracks.removeLast()
            lookedTracks.add(0, track)
        }

        /**
         * Возвращщает массив треков из [App.sharedPreferences].
         */
        fun getSavedTracks(): Array<Track>? =
            Gson().fromJson(
                sharedPreferences.getString(LOOKED_TRACKS, null),
                Array<Track>::class.java
            )

        fun clearHistory() {
            lookedTracks.clear()
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

        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}