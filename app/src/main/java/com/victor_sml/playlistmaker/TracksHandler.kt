package com.victor_sml.playlistmaker

import android.content.SharedPreferences
import android.view.View
import android.view.View.VISIBLE
import android.view.View.GONE
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

/**
 * [recyclerView] - отображает список найденных треков и историю их просмотра.
 * [nothingFound] - плейсхолдер для сообщения "Ничего не нашлось".
 * [connectionFailure] - плейсхолдер для сообщения "Проблемы со связью".
 */
class TracksHandler(
    val recyclerView: RecyclerView,
    val nothingFound: View,
    val connectionFailure: View,
    private val historyTitle: TextView,
) {
    /**
     * Хранит true если [recyclerView] отображает историю просмотренных треков.
     */
    private var isHistory = false

    private val app = recyclerView.context.applicationContext as App
    private val adapterDelegates = arrayListOf(
        TrackDelegate(this),
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
        HISTORY(VISIBLE, GONE, GONE, VISIBLE)
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
    }
}