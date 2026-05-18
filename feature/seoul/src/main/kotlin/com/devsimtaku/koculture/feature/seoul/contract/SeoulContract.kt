package com.devsimtaku.koculture.feature.seoul.contract

import com.devsimtaku.koculture.core.domain.model.CulturalEvent

data class SeoulUiState(
    val title: String = "서울 문화행사",
)

sealed interface SeoulUiEvent {
    data class OnEventClick(
        val culturalEvent: CulturalEvent,
    ) : SeoulUiEvent
}

sealed interface SeoulUiEffect {
    data class NavigateToDetail(
        val culturalEvent: CulturalEvent,
    ) : SeoulUiEffect
}
