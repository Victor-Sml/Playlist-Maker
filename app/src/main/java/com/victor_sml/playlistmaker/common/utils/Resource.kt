package com.victor_sml.playlistmaker.common.utils

import com.victor_sml.playlistmaker.common.utils.Resource.State.EMPTY
import com.victor_sml.playlistmaker.common.utils.Resource.State.SUCCESS

sealed class Resource<T>(val data: T? = null, val responseState: ResponseState) {
    operator fun component1() = data
    operator fun component2() = responseState

    class Empty<T>: Resource<T>(null, EMPTY)
    class Success<T>(data: T): Resource<T>(data, SUCCESS)
    class Error<T>(responseState: ErrorState): Resource<T>(null, responseState)

    interface ResponseState

    enum class State: ResponseState {
        SUCCESS,
        EMPTY
    }

    enum class ErrorState: ResponseState {
        CONNECTION_FAILURE,
        NOTHING_FOUND
    }
}