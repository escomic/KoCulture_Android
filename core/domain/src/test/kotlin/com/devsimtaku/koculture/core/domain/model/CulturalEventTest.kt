package com.devsimtaku.koculture.core.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CulturalEventTest {

    @Test
    fun `무료 행사 여부를 Boolean 값으로 표현한다`() {
        val freeEvent = culturalEvent(isFree = true)
        val paidEvent = culturalEvent(isFree = false)

        assertTrue(freeEvent.isFree)
        assertFalse(paidEvent.isFree)
    }

    private fun culturalEvent(
        isFree: Boolean,
    ): CulturalEvent {
        return CulturalEvent(
            codeName = "전시/미술",
            district = "강남구",
            title = "서울일러스트레이션페어",
            date = "2026-07-30~2026-08-02",
            place = "서울 코엑스 C홀",
            organizationName = "기타",
            targetUser = "누구나",
            fee = "성인 : 15,000원",
            inquiry = "070-7682-6720",
            player = "",
            program = "",
            description = "",
            organizationLink = "https://example.com",
            imageUrl = "https://example.com/image.jpg",
            registrationDate = "2026-04-24",
            ticket = "시민",
            startDate = "2026-07-30 00:00:00.0",
            endDate = "2026-08-02 00:00:00.0",
            themeCode = "기타",
            longitude = "127.059159043842",
            latitude = "37.5118239121138",
            isFree = isFree,
            detailUrl = "https://culture.seoul.go.kr/example",
            time = "10:00 ~ 18:00",
        )
    }
}
