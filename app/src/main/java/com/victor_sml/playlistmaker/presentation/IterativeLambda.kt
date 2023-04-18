package com.victor_sml.playlistmaker.presentation

interface IterativeLambda {
    fun start(delayMillis: Long, lambda: () -> Unit)
    fun stop()
}