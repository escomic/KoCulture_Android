package com.devsimtaku.koculture.core.domain.model

class KoCultureApiException(
    val code: String,
    override val message: String,
) : RuntimeException(message)
