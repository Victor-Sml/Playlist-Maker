package com.victor_sml.playlistmaker.search.data.network

import com.victor_sml.playlistmaker.search.data.dto.TracksLookupResponse
import com.victor_sml.playlistmaker.search.data.dto.TracksSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesAPIService {
    @GET("/search?entity=song")
    suspend fun searchTracks(@Query("term") term: String) : TracksSearchResponse

    @GET("lookup")
    suspend fun getTracks(@Query("id") id: String): TracksLookupResponse
}