package com.devsimtaku.koculture.core.network.model

import com.devsimtaku.koculture.core.domain.model.KoCultureApiException
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

        assertEquals("ERROR-300", exception.code)
        assertEquals("필수 값이 누락되었습니다", exception.message)
    }
}
