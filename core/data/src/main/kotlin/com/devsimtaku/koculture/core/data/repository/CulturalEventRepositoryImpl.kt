package com.devsimtaku.koculture.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.devsimtaku.koculture.core.data.repository.paging.CulturalEventPagingSource
import com.devsimtaku.koculture.core.domain.model.CulturalEvent
import com.devsimtaku.koculture.core.domain.repository.CulturalEventRepository
import com.devsimtaku.koculture.core.network.CulturalEventDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CulturalEventRepositoryImpl @Inject constructor(
    private val culturalEventDataSource: CulturalEventDataSource,
) : CulturalEventRepository {

    override fun getCulturalEvents(
        codeName: String?,
        title: String?,
        date: String?,
    ): Flow<PagingData<CulturalEvent>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                CulturalEventPagingSource(
                    culturalEventDataSource = culturalEventDataSource,
                    codeName = codeName,
                    title = title,
                    date = date,
                )
            },
        ).flow
    }

    private companion object {
        private const val PAGE_SIZE = 20
    }
}
