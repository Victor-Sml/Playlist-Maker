package com.victor_sml.playlistmaker.common.utils

import android.icu.text.Transliterator

const val TRANS_CYRILLIC_LATIN_ID = "Cyrillic-Latin"

object Transliterator {
    private val toLatinTrans = Transliterator.getInstance(TRANS_CYRILLIC_LATIN_ID)

    fun String.toLatin() = toLatinTrans.transliterate(this)
}