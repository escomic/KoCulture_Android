package com.devsimtaku.koculture.core.network.retrofit

import com.devsimtaku.koculture.core.network.model.CulturalEventInfo
import com.devsimtaku.koculture.core.network.model.CulturalEventResponse
import com.devsimtaku.koculture.core.network.model.SeoulApiResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RetrofitCulturalEventDataSourceTest {

    @Test
    fun `필수 요청 인자로 문화행사 목록 URL을 만든다`() = runTest {
        val api = RecordingKoCultureApi()
        val dataSource = RetrofitCulturalEventDataSource(
            koCultureApi = api,
            serviceKey = "test-key",
        )

        dataSource.getCulturalEvents(
            startIndex = 1,
            endIndex = 20,
        )

        assertEquals(
            "http://openapi.seoul.go.kr:8088/test-key/json/culturalEventInfo/1/20",
            api.recordedUrl,
        )
    }

    @Test
    fun `날짜 필터만 있는 경우 앞선 선택 인자 경로를 빈 값으로 유지한다`() = runTest {
        val api = RecordingKoCultureApi()
        val dataSource = RetrofitCulturalEventDataSource(
            koCultureApi = api,
            serviceKey = "test-key",
        )

        dataSource.getCulturalEvents(
            startIndex = 21,
            endIndex = 40,
            codeName = null,
            title = null,
            date = "2026-07-30",
        )

        assertEquals(
            "http://openapi.seoul.go.kr:8088/test-key/json/culturalEventInfo/21/40///2026-07-30",
            api.recordedUrl,
        )
    }

    @Test
    fun `선택 인자의 한글 경로를 인코딩한다`() = runTest {
        val api = RecordingKoCultureApi()
        val dataSource = RetrofitCulturalEventDataSource(
            koCultureApi = api,
            serviceKey = "test-key",
        )

        dataSource.getCulturalEvents(
            startIndex = 1,
            endIndex = 20,
            codeName = "전시/미술",
            title = "서울 일러스트",
        )

        assertEquals(
            "http://openapi.seoul.go.kr:8088/test-key/json/culturalEventInfo/1/20/%EC%A0%84%EC%8B%9C%2F%EB%AF%B8%EC%88%A0/%EC%84%9C%EC%9A%B8%20%EC%9D%BC%EB%9F%AC%EC%8A%A4%ED%8A%B8",
            api.recordedUrl,
        )
    }

    private class RecordingKoCultureApi : KoCultureApi {
        var recordedUrl: String? = null

        override suspend fun getCulturalEvents(url: String): CulturalEventResponse {
            recordedUrl = url
            return CulturalEventResponse(
                culturalEventInfo = CulturalEventInfo(
                    totalCount = 0,
                    result = SeoulApiResult(
                        code = "INFO-000",
                        message = "정상 처리되었습니다",
                    ),
                    rows = emptyList(),
                ),
            )
        }
    }
}
