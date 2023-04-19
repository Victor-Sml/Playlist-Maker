package com.victor_sml.playlistmaker.presentation

interface IterativeLambda {
    val delayMillis: Long
    val lambda: () -> Unit

    fun start()
    fun stop()
}