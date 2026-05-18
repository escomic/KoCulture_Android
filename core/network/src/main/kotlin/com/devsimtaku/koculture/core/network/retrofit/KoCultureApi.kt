package com.devsimtaku.koculture.core.network.retrofit

import com.devsimtaku.koculture.core.network.model.CulturalEventResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface KoCultureApi {
    @GET
    suspend fun getCulturalEvents(
        @Url url: String,
    ): CulturalEventResponse
}
