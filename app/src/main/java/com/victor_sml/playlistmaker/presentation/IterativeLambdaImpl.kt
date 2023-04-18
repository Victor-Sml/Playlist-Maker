package com.victor_sml.playlistmaker.presentation

import android.os.Handler
import android.os.Looper

class IterativeLambdaImpl : IterativeLambda {
    private val handler = Handler(Looper.getMainLooper())
    private var lambdaIterator: Runnable? = null

    override fun start(delayMillis: Long, lambda: () -> Unit) {
        val lambdaIterator = object : Runnable {
            override fun run() {
                lambda()
                handler.postDelayed(this, delayMillis)
            }
        }
        this.lambdaIterator = lambdaIterator
        lambdaIterator.run()
    }

    override fun stop() {
        lambdaIterator?.let { handler.removeCallbacks(it) }
    }
}