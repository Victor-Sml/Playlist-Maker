package com.victor_sml.playlistmaker.search.ui.stateholders

import com.victor_sml.playlistmaker.search.ui.view.recycler.api.RecyclerItem

sealed class SearchScreenState(val items: ArrayList<RecyclerItem>? = null) {
    object Empty : SearchScreenState()
    object Loading : SearchScreenState()

    class SearchResult(items: ArrayList<RecyclerItem>) : SearchScreenState(items)

    class History(items: ArrayList<RecyclerItem>) : SearchScreenState(items)

    class NothingFound(items: ArrayList<RecyclerItem>) : SearchScreenState(items)

    class ConnectionFailure(items: ArrayList<RecyclerItem>) : SearchScreenState(items)
}

