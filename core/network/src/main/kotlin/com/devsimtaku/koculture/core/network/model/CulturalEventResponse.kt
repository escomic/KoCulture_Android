package com.devsimtaku.koculture.core.network.model

import com.devsimtaku.koculture.core.domain.model.KoCultureApiException
import com.devsimtaku.koculture.core.domain.model.KoCultureApiErrorCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CulturalEventResponse(
    @SerialName("culturalEventInfo")
    val culturalEventInfo: CulturalEventInfo? = null,
    @SerialName("RESULT")
    val result: SeoulApiResult? = null,
)

@Serializable
data class CulturalEventInfo(
    @SerialName("list_total_count")
    val totalCount: Int = 0,
    @SerialName("RESULT")
    val result: SeoulApiResult,
    @SerialName("row")
    val rows: List<CulturalEventItem> = emptyList(),
)

@Serializable
data class SeoulApiResult(
    @SerialName("CODE")
    val code: String,
    @SerialName("MESSAGE")
    val message: String,
)

fun CulturalEventResponse.getOrThrow(): CulturalEventInfo {
    val result = culturalEventInfo?.result ?: result
        ?: throw KoCultureApiException(
            errorCode = KoCultureApiErrorCode.Unknown,
            rawCode = KoCultureApiErrorCode.Unknown.code,
            message = KoCultureApiErrorCode.Unknown.defaultMessage,
        )
    val errorCode = KoCultureApiErrorCode.fromCode(result.code)

    if (errorCode == KoCultureApiErrorCode.Normal && culturalEventInfo != null) {
        return culturalEventInfo
    }

    throw KoCultureApiException(
        errorCode = errorCode,
        rawCode = result.code,
        message = result.message,
    )
}
