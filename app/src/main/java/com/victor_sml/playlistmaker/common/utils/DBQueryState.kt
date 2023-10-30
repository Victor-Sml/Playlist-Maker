package com.victor_sml.playlistmaker.common.utils

sealed class DBQueryState {
    open class Ok: DBQueryState()
    object Added: Ok()

    open class Error: DBQueryState()
    object ErrorUnique: Error()
}