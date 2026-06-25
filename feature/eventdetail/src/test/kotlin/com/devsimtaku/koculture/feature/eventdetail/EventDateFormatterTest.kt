package com.devsimtaku.koculture.feature.eventdetail

import org.junit.Assert.assertEquals
import org.junit.Test

class EventDateFormatterTest {

    @Test
    fun `시작일과 종료일에서 시간 부분을 제거한다`() {
        val result = formatDateRange(
            startDate = "2026-05-22 00:00:00",
            endDate = "2026-05-31 00:00:00.0",
            fallbackDate = "",
        )

        assertEquals("2026-05-22 ~ 2026-05-31", result)
    }

    @Test
    fun `날짜만 있는 값은 그대로 표시한다`() {
        val result = formatDateRange(
            startDate = "2026-05-22",
            endDate = "2026-05-31",
            fallbackDate = "",
        )

        assertEquals("2026-05-22 ~ 2026-05-31", result)
    }

    @Test
    fun `시작일과 종료일이 같으면 하나의 날짜만 표시한다`() {
        val result = formatDateRange(
            startDate = "2026-05-22 00:00:00",
            endDate = "2026-05-22 00:00:00.0",
            fallbackDate = "",
        )

        assertEquals("2026-05-22", result)
    }

    @Test
    fun `날짜 형식이 아니면 원본 값을 유지한다`() {
        val result = formatDateRange(
            startDate = "상시",
            endDate = "",
            fallbackDate = "",
        )

        assertEquals("상시", result)
    }

    @Test
    fun `시작일과 종료일이 없으면 fallback 날짜를 표시한다`() {
        val result = formatDateRange(
            startDate = "",
            endDate = " ",
            fallbackDate = " 2026.05.22 ~ 2026.05.31 ",
        )

        assertEquals("2026.05.22 ~ 2026.05.31", result)
    }
}
