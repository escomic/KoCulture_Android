package com.devsimtaku.koculture.core.network.model

import com.devsimtaku.koculture.core.domain.model.KoCultureApiException
import com.devsimtaku.koculture.core.domain.model.KoCultureApiErrorCode
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class CulturalEventResponseTest {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Test
    fun `요청 결과가 성공이면 응답 본문을 반환한다`() {
        val response = json.decodeFromString<CulturalEventResponse>(
            """
            {
              "culturalEventInfo": {
                "list_total_count": 1,
                "RESULT": {
                  "CODE": "INFO-000",
                  "MESSAGE": "정상 처리되었습니다"
                },
                "row": [
                  {
                    "CODENAME": "전시/미술",
                    "GUNAME": "강남구",
                    "TITLE": "서울일러스트레이션페어V.21",
                    "DATE": "2026-07-30~2026-08-02",
                    "IS_FREE": "무료"
                  }
                ]
              }
            }
            """.trimIndent(),
        )

        val body = response.getOrThrow()

        assertEquals(1, body.totalCount)
        assertEquals("서울일러스트레이션페어V.21", body.rows.first().title)
    }

    @Test
    fun `요청 결과가 실패이면 API 예외를 던진다`() {
        val response = json.decodeFromString<CulturalEventResponse>(
            """
            {
              "culturalEventInfo": {
                "list_total_count": 0,
                "RESULT": {
                  "CODE": "ERROR-300",
                  "MESSAGE": "필수 값이 누락되었습니다"
                },
                "row": []
              }
            }
            """.trimIndent(),
        )

        val exception = assertThrows(KoCultureApiException::class.java) {
            response.getOrThrow()
        }

        assertEquals(KoCultureApiErrorCode.MissingRequiredValue, exception.errorCode)
        assertEquals("ERROR-300", exception.rawCode)
        assertEquals("필수 값이 누락되었습니다", exception.message)
    }

    @Test
    fun `최상위 RESULT 에러 응답이면 API 예외를 던진다`() {
        val response = json.decodeFromString<CulturalEventResponse>(
            """
            {
              "RESULT": {
                "CODE": "ERROR-500",
                "MESSAGE": "서버 오류입니다.\n지속적으로 발생시 열린 데이터 광장으로 문의(Q&A) 바랍니다."
              }
            }
            """.trimIndent(),
        )

        val exception = assertThrows(KoCultureApiException::class.java) {
            response.getOrThrow()
        }

        assertEquals(KoCultureApiErrorCode.ServerError, exception.errorCode)
        assertEquals("ERROR-500", exception.rawCode)
        assertEquals(
            "서버 오류입니다.\n지속적으로 발생시 열린 데이터 광장으로 문의(Q&A) 바랍니다.",
            exception.message,
        )
    }
}
