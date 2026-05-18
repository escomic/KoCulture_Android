package com.devsimtaku.koculture.feature.seoul

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.devsimtaku.koculture.core.domain.model.CulturalEvent
import com.devsimtaku.koculture.core.domain.repository.CulturalEventRepository
import com.devsimtaku.koculture.core.ui.mvi.BaseViewModel
import com.devsimtaku.koculture.feature.seoul.contract.SeoulUiEffect
import com.devsimtaku.koculture.feature.seoul.contract.SeoulUiEvent
import com.devsimtaku.koculture.feature.seoul.contract.SeoulUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

@HiltViewModel
class SeoulCultureViewModel @Inject constructor(
    culturalEventRepository: CulturalEventRepository,
) : BaseViewModel<SeoulUiState, SeoulUiEvent, SeoulUiEffect>(
    SeoulUiState(),
) {
    val culturalEvents: Flow<PagingData<CulturalEvent>> = culturalEventRepository
        .getCulturalEvents()
        .cachedIn(viewModelScope)

    override fun handleEvent(event: SeoulUiEvent) {
        when (event) {
            is SeoulUiEvent.OnEventClick -> {
                sendEffect(SeoulUiEffect.NavigateToDetail(event.culturalEvent))
            }
        }
    }
}
