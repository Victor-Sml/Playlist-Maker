package com.victor_sml.playlistmaker.common.utils

sealed class DBQueryState {
    object Ok: DBQueryState()
    open class Error: DBQueryState()
    object ErrorUnique: Error()
}

