package com.victor_sml.playlistmaker

import retrofit2.Call
import retrofit2.http.*

interface ItunesAPI {
    @GET("/search?entity=song")
    fun getTracks(@Query("term") term: String) : Call<TracksResponse>
}