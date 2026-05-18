package com.devsimtaku.koculture.core.data.mapper

import com.devsimtaku.koculture.core.domain.model.CulturalEvent
import com.devsimtaku.koculture.core.network.model.CulturalEventItem

fun CulturalEventItem.asDomain(): CulturalEvent {
    return CulturalEvent(
        codeName = codeName,
        district = district,
        title = title,
        date = date,
        place = place,
        organizationName = organizationName,
        targetUser = targetUser,
        fee = fee,
        inquiry = inquiry,
        player = player,
        program = program,
        description = description,
        organizationLink = organizationLink,
        imageUrl = imageUrl,
        registrationDate = registrationDate,
        ticket = ticket,
        startDate = startDate,
        endDate = endDate,
        themeCode = themeCode,
        longitude = longitude,
        latitude = latitude,
        isFree = isFree == "무료",
        detailUrl = detailUrl,
        time = time,
    )
}
