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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class RetrofitNetworkClient(
    private val iTunesService: ItunesAPIService,
    private var context: Context,
) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        if (!isConnected()) return Response().apply { resultCode = -1 }

        return withContext(Dispatchers.IO) {
            try {
                when (dto) {
                    is TracksSearchRequest -> iTunesService.searchTracks(dto.expression)
                        .apply { resultCode = 200 }
                    is TracksLookupRequest -> iTunesService.getTracks(dto.expression)
                        .apply { resultCode = 200 }
                    else -> Response().apply { resultCode = 400 }
                }
            } catch (e: Throwable) {
                if (e is HttpException) {
                    Response().apply { resultCode = e.code() }
                } else {
                    Response().apply { resultCode = -1 }
                }
            }
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