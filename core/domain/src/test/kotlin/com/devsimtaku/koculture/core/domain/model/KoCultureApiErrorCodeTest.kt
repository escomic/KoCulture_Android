package com.devsimtaku.koculture.core.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class KoCultureApiErrorCodeTest {

    @Test
    fun `응답 코드 문자열을 에러 타입으로 변환한다`() {
        val expectedMappings = mapOf(
            "INFO-000" to KoCultureApiErrorCode.Normal,
            "ERROR-300" to KoCultureApiErrorCode.MissingRequiredValue,
            "INFO-100" to KoCultureApiErrorCode.InvalidAuthenticationKey,
            "ERROR-301" to KoCultureApiErrorCode.InvalidFileType,
            "ERROR-310" to KoCultureApiErrorCode.ServiceNotFound,
            "ERROR-331" to KoCultureApiErrorCode.InvalidStartIndex,
            "ERROR-332" to KoCultureApiErrorCode.InvalidEndIndex,
            "ERROR-333" to KoCultureApiErrorCode.InvalidIndexType,
            "ERROR-334" to KoCultureApiErrorCode.StartIndexGreaterThanEndIndex,
            "ERROR-335" to KoCultureApiErrorCode.SampleDataLimitExceeded,
            "ERROR-336" to KoCultureApiErrorCode.RequestLimitExceeded,
            "ERROR-500" to KoCultureApiErrorCode.ServerError,
            "ERROR-600" to KoCultureApiErrorCode.DatabaseConnectionError,
            "ERROR-601" to KoCultureApiErrorCode.SqlError,
            "INFO-200" to KoCultureApiErrorCode.NoData,
        )

        expectedMappings.forEach { (rawCode, errorCode) ->
            assertEquals(errorCode, KoCultureApiErrorCode.fromCode(rawCode))
        }
    }

    @Test
    fun `정의되지 않은 응답 코드는 Unknown 으로 변환한다`() {
        val errorCode = KoCultureApiErrorCode.fromCode("INFO-999")

        assertEquals(KoCultureApiErrorCode.Unknown, errorCode)
    }
}
