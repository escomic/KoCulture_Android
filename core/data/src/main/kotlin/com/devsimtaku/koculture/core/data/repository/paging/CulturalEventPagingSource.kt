package com.devsimtaku.koculture.core.data.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.devsimtaku.koculture.core.data.mapper.asDomain
import com.devsimtaku.koculture.core.domain.model.CulturalEvent
import com.devsimtaku.koculture.core.network.CulturalEventDataSource
import com.devsimtaku.koculture.core.network.model.getOrThrow

class CulturalEventPagingSource(
    private val culturalEventDataSource: CulturalEventDataSource,
    private val codeName: String?,
    private val title: String?,
    private val date: String?,
) : PagingSource<Int, CulturalEvent>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CulturalEvent> {
        val page = params.key ?: START_PAGE
        val startIndex = ((page - 1) * params.loadSize) + 1
        val endIndex = startIndex + params.loadSize - 1

        return try {
            val response = culturalEventDataSource.getCulturalEvents(
                startIndex = startIndex,
                endIndex = endIndex,
                codeName = codeName,
                title = title,
                date = date,
            ).getOrThrow()
            val items = response.rows.map { it.asDomain() }

            LoadResult.Page(
                data = items,
                prevKey = if (page == START_PAGE) null else page - 1,
                nextKey = if (endIndex >= response.totalCount || items.size < params.loadSize) {
                    null
                } else {
                    page + 1
                },
            )
        } catch (throwable: Throwable) {
            LoadResult.Error(throwable)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CulturalEvent>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    private companion object {
        private const val START_PAGE = 1
    }
}
