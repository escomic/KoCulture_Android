package com.devsimtaku.koculture.feature.eventdetail.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.devsimtaku.koculture.feature.eventdetail.EventDetailScreen

fun EntryProviderScope<NavKey>.eventDetailEntry(
    onBackClick: () -> Unit,
) {
    entry<EventDetailNavKey> { key ->
        EventDetailScreen(
            culturalEvent = key.culturalEvent,
            onBackClick = onBackClick,
        )
    }
}
