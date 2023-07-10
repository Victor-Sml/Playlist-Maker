package com.victor_sml.playlistmaker.common.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun <T> debounce(
    delayMillis: Long,
    coroutineScope: CoroutineScope,
    action: (T) -> Unit,
): (T) -> Unit {
    var isClickAllowed = true
    return { param: T ->
        coroutineScope.launch {
            if (isClickAllowed) {
                action(param)
                isClickAllowed = false
                delay(delayMillis)
                isClickAllowed = true
            }
        }
    }
}