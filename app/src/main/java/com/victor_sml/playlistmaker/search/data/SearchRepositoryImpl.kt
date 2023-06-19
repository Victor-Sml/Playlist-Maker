package com.victor_sml.playlistmaker.search.data

import com.victor_sml.playlistmaker.common.models.Track
import com.victor_sml.playlistmaker.search.domain.api.SearchRepository
import com.victor_sml.playlistmaker.common.utils.Resource
import com.victor_sml.playlistmaker.common.utils.Resource.ResponseState.CONNECTION_FAILURE
import com.victor_sml.playlistmaker.common.utils.Resource.ResponseState.NOTHING_FOUND
import com.victor_sml.playlistmaker.search.data.api.NetworkClient
import com.victor_sml.playlistmaker.search.data.dto.TrackDto
import com.victor_sml.playlistmaker.search.data.dto.TracksLookupRequest
import com.victor_sml.playlistmaker.search.data.dto.TracksLookupResponse
import com.victor_sml.playlistmaker.search.data.dto.TracksSearchRequest
import com.victor_sml.playlistmaker.search.data.dto.TracksSearchResponse

class SearchRepositoryImpl(private val networkClient: NetworkClient) : SearchRepository {
    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        if (response.resultCode in 200..299) {
            val results = (response as TracksSearchResponse).results
            return handleSuccessResponse(results)
        }
        return handleErrorResponse(response.resultCode)
    }

    override fun lookupTracks(trackIds: Array<Int>): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksLookupRequest(trackIds))

        if (response.resultCode in 200..299) {
            val results = (response as TracksLookupResponse).results
            return handleSuccessResponse(results)
        }
        return handleErrorResponse(response.resultCode)
    }

    private fun handleSuccessResponse(results: List<TrackDto>): Resource<List<Track>> {
        if (results.isNotEmpty()) return Resource.Success(results.map { it.mapToTrack() })
        return Resource.Error(NOTHING_FOUND)
    }

    private fun handleErrorResponse(responseStatusCode: Int): Resource<List<Track>> {
        return when (responseStatusCode) {
            -1 -> Resource.Error(CONNECTION_FAILURE)
            404 -> Resource.Error(NOTHING_FOUND)
            else -> Resource.Error(CONNECTION_FAILURE)
        }
    }
}