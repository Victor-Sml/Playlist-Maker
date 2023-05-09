package com.victor_sml.playlistmaker.common.utils

import android.os.Handler
import android.os.Looper

class IterativeLambdaIml : IterativeLambda {
    private var delayMillis: Long = 0L
    private lateinit var lambda: () -> Unit
    private lateinit var lambdaIterator: Runnable
    private val handler = Handler(Looper.getMainLooper())

    override fun initialize(delayMillis: Long, lambda: () -> Unit) {
        this.delayMillis = delayMillis
        this.lambda = lambda

        lambdaIterator = object : Runnable {
            override fun run() {
                lambda()
                handler.postDelayed(this, delayMillis)
            }
        }
    }

    override fun start() {
        if (this::lambdaIterator.isInitialized) lambdaIterator.run()
    }

    override fun stop() {
        if (this::lambdaIterator.isInitialized) handler.removeCallbacks(lambdaIterator)
    }
}