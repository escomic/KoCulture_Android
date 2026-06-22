package com.devsimtaku.koculture.core.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CultureMap(
    coordinate: MapCoordinate,
    modifier: Modifier = Modifier,
    provider: MapProvider = MapProvider.Naver,
) {
    when (provider) {
        MapProvider.Naver -> NaverCultureMap(
            coordinate = coordinate,
            modifier = modifier,
        )
    }
}
