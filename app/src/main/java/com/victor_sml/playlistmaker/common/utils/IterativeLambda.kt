package com.victor_sml.playlistmaker.common.utils

interface IterativeLambda {
    val delayMillis: Long
    val lambda: () -> Unit

    fun start()
    fun stop()
}