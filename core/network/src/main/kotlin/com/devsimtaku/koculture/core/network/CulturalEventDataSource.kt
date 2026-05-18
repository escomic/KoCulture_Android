package com.devsimtaku.koculture.core.network

import com.devsimtaku.koculture.core.network.model.CulturalEventResponse

interface CulturalEventDataSource {
    suspend fun getCulturalEvents(
        startIndex: Int,
        endIndex: Int,
        codeName: String? = null,
        title: String? = null,
        date: String? = null,
    ): CulturalEventResponse
}
