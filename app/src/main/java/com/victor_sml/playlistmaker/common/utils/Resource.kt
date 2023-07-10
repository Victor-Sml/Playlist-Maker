package com.victor_sml.playlistmaker.common.utils

import com.victor_sml.playlistmaker.common.utils.Resource.ResponseState.SUCCESS

sealed class Resource<T>(val data: T? = null, val responseState: ResponseState) {
    operator fun component1() = data
    operator fun component2() = responseState

    class Success<T>(data: T): Resource<T>(data, SUCCESS)
    class Error<T>(responseState: ResponseState, data: T? = null): Resource<T>(data, responseState)

    enum class ResponseState {
        SUCCESS,
        NOTHING_FOUND,
        CONNECTION_FAILURE
    }
}