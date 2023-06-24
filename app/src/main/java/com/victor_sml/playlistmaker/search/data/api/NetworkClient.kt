package com.victor_sml.playlistmaker.search.data.api

import com.victor_sml.playlistmaker.search.data.dto.Response

interface NetworkClient {
    suspend fun doRequest(dto: Any): Response
}