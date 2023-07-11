package com.victor_sml.playlistmaker.common.utils.recycler.api

interface RecyclerController {
    fun updateContent(content: ArrayList<RecyclerItem>? = null)
    fun clearContent()
}