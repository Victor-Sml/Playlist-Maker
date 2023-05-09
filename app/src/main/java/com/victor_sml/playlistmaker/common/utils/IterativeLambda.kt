package com.victor_sml.playlistmaker.common.utils

interface IterativeLambda {
    fun initialize(delayMillis: Long, lambda: () -> Unit)
    fun start()
    fun stop()
}