package com.victor_sml.playlistmaker.search.data.network

import com.victor_sml.playlistmaker.search.data.dto.TracksSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesAPIService {
    @GET("/search?entity=song")
    fun getTracks(@Query("term") term: String) : Call<TracksSearchResponse>

    @GET("lookup")
    fun getTrack(@Query("id") id: String): Call<TracksSearchResponse>
}