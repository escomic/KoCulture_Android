package com.devsimtaku.koculture.core.data.repository.paging

import androidx.paging.PagingSource
import com.devsimtaku.koculture.core.network.CulturalEventDataSource
import com.devsimtaku.koculture.core.network.model.CulturalEventInfo
import com.devsimtaku.koculture.core.network.model.CulturalEventItem
import com.devsimtaku.koculture.core.network.model.CulturalEventResponse
import com.devsimtaku.koculture.core.network.model.SeoulApiResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CulturalEventPagingSourceTest {

    @Test
    fun `페이지와 로드 크기로 시작 인덱스와 종료 인덱스를 계산한다`() = runTest {
        val dataSource = RecordingCulturalEventDataSource(
            totalCount = 100,
            rows = List(20) { index ->
                culturalEventItem(title = "서울 행사 $index")
            },
        )
        val pagingSource = CulturalEventPagingSource(
            culturalEventDataSource = dataSource,
            codeName = "전시/미술",
            title = "서울",
            date = "2026-07-30",
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 2,
                loadSize = 20,
                placeholdersEnabled = false,
            ),
        )

        assertEquals(21, dataSource.recordedStartIndex)
        assertEquals(40, dataSource.recordedEndIndex)
        assertEquals("전시/미술", dataSource.recordedCodeName)
        assertEquals("서울", dataSource.recordedTitle)
        assertEquals("2026-07-30", dataSource.recordedDate)

        val page = result as PagingSource.LoadResult.Page
        assertEquals("서울 행사 0", page.data.first().title)
        assertEquals(1, page.prevKey)
        assertEquals(3, page.nextKey)
    }

    @Test
    fun `요청 종료 인덱스가 전체 개수에 도달하면 다음 키를 반환하지 않는다`() = runTest {
        val dataSource = RecordingCulturalEventDataSource(
            totalCount = 20,
            rows = listOf(culturalEventItem(title = "마지막 행사")),
        )
        val pagingSource = CulturalEventPagingSource(
            culturalEventDataSource = dataSource,
            codeName = null,
            title = null,
            date = null,
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 20,
                placeholdersEnabled = false,
            ),
        )

        val page = result as PagingSource.LoadResult.Page
        assertNull(page.prevKey)
        assertNull(page.nextKey)
    }

    private class RecordingCulturalEventDataSource(
        private val totalCount: Int,
        private val rows: List<CulturalEventItem>,
    ) : CulturalEventDataSource {
        var recordedStartIndex: Int? = null
        var recordedEndIndex: Int? = null
        var recordedCodeName: String? = null
        var recordedTitle: String? = null
        var recordedDate: String? = null

        override suspend fun getCulturalEvents(
            startIndex: Int,
            endIndex: Int,
            codeName: String?,
            title: String?,
            date: String?,
        ): CulturalEventResponse {
            recordedStartIndex = startIndex
            recordedEndIndex = endIndex
            recordedCodeName = codeName
            recordedTitle = title
            recordedDate = date

            return CulturalEventResponse(
                culturalEventInfo = CulturalEventInfo(
                    totalCount = totalCount,
                    result = SeoulApiResult(
                        code = "INFO-000",
                        message = "정상 처리되었습니다",
                    ),
                    rows = rows,
                ),
            )
        }
    }

    private fun culturalEventItem(
        title: String,
    ): CulturalEventItem {
        return CulturalEventItem(
            title = title,
            isFree = "무료",
        )
    }
}
