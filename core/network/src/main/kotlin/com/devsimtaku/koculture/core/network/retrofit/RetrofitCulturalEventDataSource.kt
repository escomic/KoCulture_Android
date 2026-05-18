package com.devsimtaku.koculture.core.network.retrofit

import com.devsimtaku.koculture.core.network.CulturalEventDataSource
import com.devsimtaku.koculture.core.network.model.CulturalEventResponse
import okhttp3.HttpUrl.Companion.toHttpUrl

internal class RetrofitCulturalEventDataSource(
    private val koCultureApi: KoCultureApi,
    private val serviceKey: String,
) : CulturalEventDataSource {

    override suspend fun getCulturalEvents(
        startIndex: Int,
        endIndex: Int,
        codeName: String?,
        title: String?,
        date: String?,
    ): CulturalEventResponse {
        return koCultureApi.getCulturalEvents(
            url = buildCulturalEventUrl(
                startIndex = startIndex,
                endIndex = endIndex,
                codeName = codeName,
                title = title,
                date = date,
            ),
        )
    }

    private fun buildCulturalEventUrl(
        startIndex: Int,
        endIndex: Int,
        codeName: String?,
        title: String?,
        date: String?,
    ): String {
        val pathSegments = listOf(
            serviceKey,
            TYPE_JSON,
            SERVICE_NAME,
            startIndex.toString(),
            endIndex.toString(),
        ) + listOf(codeName, title, date).takeLastNonBlankPrefix()

        return BASE_URL + pathSegments.joinToString(separator = "/") { segment ->
            segment.encodePathSegment()
        }
    }

    private fun List<String?>.takeLastNonBlankPrefix(): List<String> {
        val lastIndex = indexOfLast { !it.isNullOrBlank() }
        if (lastIndex == -1) return emptyList()

        return take(lastIndex + 1).map { it.orEmpty() }
    }

    private companion object {
        private const val BASE_URL = "http://openapi.seoul.go.kr:8088/"
        private const val TYPE_JSON = "json"
        private const val SERVICE_NAME = "KoCultureAndroid"
    }
}

private fun String.encodePathSegment(): String {
    if (isEmpty()) return this

    return "http://localhost/"
        .toHttpUrl()
        .newBuilder()
        .addPathSegment(this)
        .build()
        .encodedPathSegments
        .last()
}
