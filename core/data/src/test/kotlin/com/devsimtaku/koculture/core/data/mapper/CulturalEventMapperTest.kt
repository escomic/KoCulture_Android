package com.devsimtaku.koculture.core.data.mapper

import com.devsimtaku.koculture.core.network.model.CulturalEventItem
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CulturalEventMapperTest {

    @Test
    fun `무료 문자열을 true 로 매핑한다`() {
        val event = CulturalEventItem(
            isFree = "무료",
        ).asDomain()

        assertTrue(event.isFree)
    }

    @Test
    fun `무료가 아닌 문자열을 false 로 매핑한다`() {
        val event = CulturalEventItem(
            isFree = "유료",
        ).asDomain()

        assertFalse(event.isFree)
    }
}
