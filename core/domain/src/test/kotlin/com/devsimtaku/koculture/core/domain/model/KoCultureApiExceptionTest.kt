package com.devsimtaku.koculture.core.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class KoCultureApiExceptionTest {

    @Test
    fun `API 에러 코드와 메시지를 보존한다`() {
        val exception = KoCultureApiException(
            code = "ERROR-300",
            message = "필수 값이 누락되었습니다",
        )

        assertEquals("ERROR-300", exception.code)
        assertEquals("필수 값이 누락되었습니다", exception.message)
    }
}
