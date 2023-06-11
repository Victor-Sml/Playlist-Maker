package com.victor_sml.playlistmaker.common.utils.api

interface IterativeLambda {
    fun initialize(delayMillis: Long, lambda: () -> Unit)
    fun start()
    fun stop()
}