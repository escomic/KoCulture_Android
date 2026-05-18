package com.devsimtaku.koculture.core.domain.model

class KoCultureApiException(
    val errorCode: KoCultureApiErrorCode,
    val rawCode: String,
    override val message: String,
) : RuntimeException(message)
