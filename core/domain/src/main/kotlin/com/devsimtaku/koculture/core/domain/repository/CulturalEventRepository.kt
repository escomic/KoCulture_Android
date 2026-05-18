package com.devsimtaku.koculture.core.domain.repository

import androidx.paging.PagingData
import com.devsimtaku.koculture.core.domain.model.CulturalEvent
import kotlinx.coroutines.flow.Flow

interface CulturalEventRepository {
    fun getCulturalEvents(
        codeName: String? = null,
        title: String? = null,
        date: String? = null,
    ): Flow<PagingData<CulturalEvent>>
}
