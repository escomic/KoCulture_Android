package com.devsimtaku.koculture.feature.seoul.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.devsimtaku.koculture.feature.seoul.SeoulScreen

fun EntryProviderScope<NavKey>.seoulEntry(
    onEventClick: () -> Unit,
) {
    entry<SeoulNavKey> {
        SeoulScreen(
            onEventClick = onEventClick,
        )
    }
}
