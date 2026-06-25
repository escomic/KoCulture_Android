package com.devsimtaku.koculture.core.ui.phone

import org.junit.Assert.assertEquals
import org.junit.Test

class PhoneNumberExtractorTest {

    @Test
    fun `문의 문자열에서 전화번호를 추출한다`() {
        val inquiries = mapOf(
            "02-2629-2250" to listOf("02-2629-2250"),
            "02-3274-8600 [문의1번]" to listOf("02-3274-8600"),
            "02-3274-8600 [문의1번] 평일 9:00~18:00 (토,일 공휴일 휴무)" to
                listOf("02-3274-8600"),
            "1588-1210 (서울시립교향악단)" to listOf("1588-1210"),
            "070-4239-9485" to listOf("070-4239-9485"),
            "0507-1351-4237" to listOf("0507-1351-4237"),
            "콜센터 : 02-399-1000 단체예약 : 1800-8714" to
                listOf("02-399-1000", "1800-8714"),
            "02-357-0100 (내선 3번, 문화사업)" to listOf("02-357-0100"),
            "02-399-1000 (09:00-20:00, 연중무휴)" to listOf("02-399-1000"),
            "02-307-6030(내선201)" to listOf("02-307-6030"),
            "070-4001-2800 / 070-4062-2800" to listOf("070-4001-2800", "070-4062-2800"),
            "02-2138-2762, 2764" to listOf("02-2138-2762"),
            "070-5095-9904 / 9903" to listOf("070-5095-9904"),
            "02-585-2934~6" to listOf("02-585-2934"),
            "문의-02-2629-2250" to listOf("02-2629-2250"),
            "대표번호 -1588-1210" to listOf("1588-1210"),
        )

        inquiries.forEach { (inquiry, expected) ->
            assertEquals(
                inquiry,
                expected,
                inquiry.extractPhoneNumbers().map(PhoneNumberMatch::number),
            )
        }
    }

    @Test
    fun `전화번호의 원문 범위를 반환한다`() {
        val inquiry = "콜센터 : 02-399-1000 단체예약 : 1800-8714"

        val matches = inquiry.extractPhoneNumbers()

        matches.forEach { match ->
            assertEquals(match.number, inquiry.substring(match.range))
        }
    }

    @Test
    fun `세 번째 영역 뒤에 공백 없이 이어지는 문자는 전화번호에서 제외한다`() {
        val inquiries = mapOf(
            "02-307-6030내선201" to "02-307-6030",
            "02-585-29346" to "02-585-2934",
        )

        inquiries.forEach { (inquiry, expected) ->
            assertEquals(
                expected,
                inquiry.extractPhoneNumbers().single().number,
            )
        }
    }

    @Test
    fun `전화번호 형식이 아니면 추출하지 않는다`() {
        val inquiries = listOf(
            "",
            "문의 번호 없음",
            "2-123-4567",
            "02-12-3456",
            "02-1234-567",
            "1-234-5678",
        )

        inquiries.forEach { inquiry ->
            assertEquals(emptyList<PhoneNumberMatch>(), inquiry.extractPhoneNumbers())
        }
    }
}
