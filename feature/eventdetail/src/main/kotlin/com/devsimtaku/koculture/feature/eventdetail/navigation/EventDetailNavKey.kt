package com.devsimtaku.koculture.feature.eventdetail.navigation

import androidx.navigation3.runtime.NavKey
import com.devsimtaku.koculture.core.domain.model.CulturalEvent
import kotlinx.serialization.Serializable

@Serializable
data class EventDetailNavKey(
    val culturalEvent: CulturalEvent,
) : NavKey
