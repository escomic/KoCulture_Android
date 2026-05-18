package com.devsimtaku.koculture.feature.seoul.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.devsimtaku.koculture.core.domain.model.CulturalEvent
import com.devsimtaku.koculture.feature.seoul.SeoulCultureScreen

fun EntryProviderScope<NavKey>.seoulEntry(
    onEventClick: (CulturalEvent) -> Unit,
) {
    entry<SeoulNavKey> {
        SeoulCultureScreen(
            onEventClick = onEventClick,
        )
    }
}
