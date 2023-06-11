package com.victor_sml.playlistmaker.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import com.victor_sml.playlistmaker.search.data.api.NetworkClient
import com.victor_sml.playlistmaker.search.data.dto.Response
import com.victor_sml.playlistmaker.search.data.dto.TracksLookupRequest
import com.victor_sml.playlistmaker.search.data.dto.TracksSearchRequest
import java.io.IOException

class RetrofitNetworkClient(
    private val iTunesService: ItunesAPIService,
    private var context: Context
) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (!isConnected()) return Response().apply { resultCode = -1 }

        try {
            val response = when (dto) {
                is TracksSearchRequest -> iTunesService.searchTracks(dto.expression).execute()
                is TracksLookupRequest -> iTunesService.getTracks(dto.expression).execute()
                else -> return Response().apply { resultCode = 400 }
            }
            val body = response.body()
            return if (body != null) {
                body.apply { resultCode = response.code() }
            } else {
                Response().apply { resultCode = response.code() }
            }
        } catch (e: IOException) {
            return Response().apply { resultCode = -1 }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }

    companion object {
        const val ITUNES_BASE_URL = "https://itunes.apple.com"
    }
}