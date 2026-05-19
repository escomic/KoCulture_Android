package com.devsimtaku.koculture.feature.seoul.contract

import com.devsimtaku.koculture.core.domain.model.CulturalEvent

data class SeoulUiState(
    val title: String = "서울 문화행사",
    val selectedCategory: SeoulCultureCategory = SeoulCultureCategory.All,
    val isFreeOnly: Boolean = false,
)

enum class SeoulCultureCategory(
    val label: String,
    val codeName: String?,
) {
    All(label = "전체", codeName = null),
    KoreanTraditionalMusic(label = "국악", codeName = "국악"),
    Dance(label = "무용", codeName = "무용"),
    Theater(label = "연극", codeName = "연극"),
    Movie(label = "영화", codeName = "영화"),
    Classic(label = "클래식", codeName = "클래식"),
    Concert(label = "콘서트", codeName = "콘서트"),
    Etc(label = "기타", codeName = "기타"),
    SoloRecital(label = "독주/독창회", codeName = "독주/독창회"),
    ExhibitionArt(label = "전시/미술", codeName = "전시/미술"),
    MusicalOpera(label = "뮤지컬/오페라", codeName = "뮤지컬/오페라"),
    EducationExperience(label = "교육/체험", codeName = "교육/체험"),
    FestivalCultureArt(label = "축제-문화/예술", codeName = "축제-문화/예술"),
    FestivalTraditionHistory(label = "축제-전통/역사", codeName = "축제-전통/역사"),
    FestivalTourismSports(label = "축제-관광/체육", codeName = "축제-관광/체육"),
    FestivalNatureLandscape(label = "축제-자연/경관", codeName = "축제-자연/경관"),
    FestivalEtc(label = "축제-기타", codeName = "축제-기타"),
}

sealed interface SeoulUiEvent {
    data class OnEventClick(
        val culturalEvent: CulturalEvent,
    ) : SeoulUiEvent

    data class OnCategorySelect(
        val category: SeoulCultureCategory,
    ) : SeoulUiEvent

    data class OnFreeOnlyChange(
        val isFreeOnly: Boolean,
    ) : SeoulUiEvent
}

sealed interface SeoulUiEffect {
    data class NavigateToDetail(
        val culturalEvent: CulturalEvent,
    ) : SeoulUiEffect
}
