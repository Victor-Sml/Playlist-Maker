package com.victor_sml.playlistmaker.search.domain

import com.victor_sml.playlistmaker.search.domain.api.SearchInteractor
import com.victor_sml.playlistmaker.search.domain.api.SearchInteractor.SearchResultConsumer
import com.victor_sml.playlistmaker.search.domain.api.SearchRepository
import java.util.concurrent.Executors

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: SearchResultConsumer) {
        executor.execute {
            val resource = repository.searchTracks(expression)
            val resourceData = resource.data?.filterNot { it.previewUrl.isNullOrEmpty() }
            consumer.consume(resourceData, resource.responseState)
        }
    }
}