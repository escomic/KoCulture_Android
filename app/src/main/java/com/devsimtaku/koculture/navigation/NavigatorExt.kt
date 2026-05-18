package com.devsimtaku.koculture.navigation

import com.devsimtaku.koculture.core.navigation.Navigator
import com.devsimtaku.koculture.feature.eventdetail.navigation.EventDetailNavKey

fun Navigator.navigateToEventDetail() {
    navigate(
        key = EventDetailNavKey,
    )
}
