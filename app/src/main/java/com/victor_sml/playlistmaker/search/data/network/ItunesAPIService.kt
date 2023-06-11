package com.victor_sml.playlistmaker.search.data.network

import com.victor_sml.playlistmaker.search.data.dto.TracksLookupResponse
import com.victor_sml.playlistmaker.search.data.dto.TracksSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesAPIService {
    @GET("/search?entity=song")
    fun searchTracks(@Query("term") term: String) : Call<TracksSearchResponse>

    @GET("lookup")
    fun getTracks(@Query("id") id: String): Call<TracksLookupResponse>
}