package com.victor_sml.playlistmaker.common.utils

import android.os.Handler
import android.os.Looper

class IterativeLambdaIml(
    override val delayMillis: Long,
    override val lambda: () -> Unit
) : IterativeLambda {
    private val handler = Handler(Looper.getMainLooper())
    private val lambdaIterator = object : Runnable {
        override fun run() {
            lambda()
            handler.postDelayed(this, delayMillis)
        }
    }

    override fun start() {
        lambdaIterator.run()
    }

    override fun stop() {
        handler.removeCallbacks(lambdaIterator)
    }
}