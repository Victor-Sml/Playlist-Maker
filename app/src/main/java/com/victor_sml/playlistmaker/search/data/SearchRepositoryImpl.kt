package com.victor_sml.playlistmaker.search.data

import com.victor_sml.playlistmaker.common.models.Track
import com.victor_sml.playlistmaker.search.data.dto.TrackDto
import com.victor_sml.playlistmaker.search.data.dto.TracksSearchResponse
import com.victor_sml.playlistmaker.search.data.dto.TracksSearchRequest
import com.victor_sml.playlistmaker.search.domain.api.SearchRepository
import com.victor_sml.playlistmaker.search.domain.Resource
import com.victor_sml.playlistmaker.search.domain.Resource.RequestState.CONNECTION_FAILURE
import com.victor_sml.playlistmaker.search.domain.Resource.RequestState.NOTHING_FOUND
import com.victor_sml.playlistmaker.search.data.api.NetworkClient

class SearchRepositoryImpl(private val networkClient: NetworkClient) : SearchRepository {
    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error(CONNECTION_FAILURE)
            }
            in 200..299 -> {
                if ((response as TracksSearchResponse).results.isEmpty()) {
                    return Resource.Error(NOTHING_FOUND)
                }
                Resource.Success(
                    response.results.map { mapToTrack(it) }
                )
            }
            404 -> Resource.Error(NOTHING_FOUND)
            else -> {
                return Resource.Error(CONNECTION_FAILURE)
            }
        }
    }

    private fun mapToTrack(track: TrackDto): Track {
        return Track(
            track.artistName,
            track.trackName,
            track.artworkUrl100,
            track.trackTimeMillis,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
    }
}
