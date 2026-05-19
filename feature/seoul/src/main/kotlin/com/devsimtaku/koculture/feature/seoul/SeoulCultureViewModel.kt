package com.devsimtaku.koculture.feature.seoul

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.devsimtaku.koculture.core.domain.model.CulturalEvent
import com.devsimtaku.koculture.core.domain.repository.CulturalEventRepository
import com.devsimtaku.koculture.core.ui.mvi.BaseViewModel
import com.devsimtaku.koculture.feature.seoul.contract.SeoulCultureCategory
import com.devsimtaku.koculture.feature.seoul.contract.SeoulUiEffect
import com.devsimtaku.koculture.feature.seoul.contract.SeoulUiEvent
import com.devsimtaku.koculture.feature.seoul.contract.SeoulUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SeoulCultureViewModel @Inject constructor(
    culturalEventRepository: CulturalEventRepository,
) : BaseViewModel<SeoulUiState, SeoulUiEvent, SeoulUiEffect>(
    SeoulUiState(),
) {
    val culturalEvents: Flow<PagingData<CulturalEvent>> = uiState
        .map { uiState ->
            FilterCondition(
                codeName = uiState.selectedCategory.codeName,
                isFreeOnly = uiState.isFreeOnly,
            )
        }
        .distinctUntilChanged()
        .flatMapLatest { condition ->
            culturalEventRepository
                .getCulturalEvents(codeName = condition.codeName)
                .map { pagingData ->
                    if (condition.isFreeOnly) {
                        pagingData.filter { culturalEvent -> culturalEvent.isFree }
                    } else {
                        pagingData
                    }
                }
            }
        .cachedIn(viewModelScope)

    override fun handleEvent(event: SeoulUiEvent) {
        when (event) {
            is SeoulUiEvent.OnEventClick -> {
                sendEffect(SeoulUiEffect.NavigateToDetail(event.culturalEvent))
            }

            is SeoulUiEvent.OnCategorySelect -> {
                selectCategory(event.category)
            }

            is SeoulUiEvent.OnFreeOnlyChange -> {
                changeFreeOnly(event.isFreeOnly)
            }
        }
    }

    private fun selectCategory(category: SeoulCultureCategory) {
        setState {
            copy(selectedCategory = category)
        }
    }

    private fun changeFreeOnly(isFreeOnly: Boolean) {
        setState {
            copy(isFreeOnly = isFreeOnly)
        }
    }

    private data class FilterCondition(
        val codeName: String?,
        val isFreeOnly: Boolean,
    )
}
