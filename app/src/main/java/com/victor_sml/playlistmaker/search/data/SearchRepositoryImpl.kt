package com.victor_sml.playlistmaker.search.data

import com.victor_sml.playlistmaker.common.data.db.AppDatabase
import com.victor_sml.playlistmaker.common.domain.models.Track
import com.victor_sml.playlistmaker.search.domain.api.SearchRepository
import com.victor_sml.playlistmaker.common.utils.Resource
import com.victor_sml.playlistmaker.common.utils.Resource.ErrorState.CONNECTION_FAILURE
import com.victor_sml.playlistmaker.common.utils.Resource.ErrorState.NOTHING_FOUND
import com.victor_sml.playlistmaker.search.data.api.NetworkClient
import com.victor_sml.playlistmaker.search.data.dto.TrackDto
import com.victor_sml.playlistmaker.search.data.dto.TracksLookupRequest
import com.victor_sml.playlistmaker.search.data.dto.TracksLookupResponse
import com.victor_sml.playlistmaker.search.data.dto.TracksSearchRequest
import com.victor_sml.playlistmaker.search.data.dto.TracksSearchResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(private val networkClient: NetworkClient, private val db: AppDatabase) :
    SearchRepository {
    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        if (response.resultCode == SUCCESSFUL_CODE) {
            val results = (response as TracksSearchResponse).results
            emit(handleSuccessResponse(results))
            return@flow
        }
        emit(handleErrorResponse(response.resultCode))
    }

    override fun lookupTracks(trackIds: IntArray): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksLookupRequest(trackIds))

        if (response.resultCode == SUCCESSFUL_CODE) {
            val results = (response as TracksLookupResponse).results
            emit(handleSuccessResponse(results))
            return@flow
        }
        emit(handleErrorResponse(response.resultCode))
    }

    private suspend fun handleSuccessResponse(results: List<TrackDto>): Resource<List<Track>> {
        if (results.isNotEmpty()) {
            val favoriteTrackIds = db.trackDao().getFavoriteTrackIds()

            val result = results.map { trackDto ->
                trackDto.mapToTrack(favoriteTrackIds.contains(trackDto.trackId))
            }

            return Resource.Success(result)
        }
        return Resource.Error(NOTHING_FOUND)
    }

    private fun handleErrorResponse(responseStatusCode: Int): Resource<List<Track>> {
        return when (responseStatusCode) {
            CONNECTION_FAILURE_CODE -> Resource.Error(CONNECTION_FAILURE)
            NOTHING_FOUND_CODE -> Resource.Error(NOTHING_FOUND)
            else -> Resource.Error(CONNECTION_FAILURE)
        }
    }

    companion object {
        const val CONNECTION_FAILURE_CODE = -1
        const val SUCCESSFUL_CODE = 200
        const val NOTHING_FOUND_CODE = 404
    }
}