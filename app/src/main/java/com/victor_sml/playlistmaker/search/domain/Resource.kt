package com.victor_sml.playlistmaker.search.domain

import com.victor_sml.playlistmaker.search.domain.Resource.RequestState.SUCCESS

sealed class Resource<T>(val data: T? = null, val responseState: RequestState) {
    class Success<T>(data: T): Resource<T>(data, SUCCESS)
    class Error<T>(responseState: RequestState, data: T? = null): Resource<T>(data, responseState)

    enum class RequestState {
        SUCCESS,
        NOTHING_FOUND,
        CONNECTION_FAILURE
    }
}

