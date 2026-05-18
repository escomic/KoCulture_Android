package com.devsimtaku.koculture.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.devsimtaku.koculture.core.navigation.Navigator
import com.devsimtaku.koculture.core.navigation.rememberNavigationState
import com.devsimtaku.koculture.core.navigation.toBackStack
import com.devsimtaku.koculture.feature.eventdetail.navigation.eventDetailEntry
import com.devsimtaku.koculture.feature.seoul.navigation.SeoulNavKey
import com.devsimtaku.koculture.feature.seoul.navigation.seoulEntry
import com.devsimtaku.koculture.navigation.navigateToEventDetail

@Composable
fun KoCultureApp() {
    val navigationState = rememberNavigationState(
        startKey = SeoulNavKey,
        topLevelKeys = remember {
            setOf(SeoulNavKey)
        },
    )
    val backStack = navigationState.toBackStack()
    val navigator = remember(navigationState) {
        Navigator(navigationState)
    }
    val entryProvider = entryProvider<NavKey> {
        seoulEntry(
            onEventClick = navigator::navigateToEventDetail,
        )
        eventDetailEntry(
            onBackClick = navigator::goBack,
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = navigator::goBack,
            entryProvider = entryProvider,
        )
    }
}
