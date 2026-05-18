package com.devsimtaku.koculture.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CulturalEventItem(
    @SerialName("CODENAME")
    val codeName: String = "",
    @SerialName("GUNAME")
    val district: String = "",
    @SerialName("TITLE")
    val title: String = "",
    @SerialName("DATE")
    val date: String = "",
    @SerialName("PLACE")
    val place: String = "",
    @SerialName("ORG_NAME")
    val organizationName: String = "",
    @SerialName("USE_TRGT")
    val targetUser: String = "",
    @SerialName("USE_FEE")
    val fee: String = "",
    @SerialName("INQUIRY")
    val inquiry: String = "",
    @SerialName("PLAYER")
    val player: String = "",
    @SerialName("PROGRAM")
    val program: String = "",
    @SerialName("ETC_DESC")
    val description: String = "",
    @SerialName("ORG_LINK")
    val organizationLink: String = "",
    @SerialName("MAIN_IMG")
    val imageUrl: String = "",
    @SerialName("RGSTDATE")
    val registrationDate: String = "",
    @SerialName("TICKET")
    val ticket: String = "",
    @SerialName("STRTDATE")
    val startDate: String = "",
    @SerialName("END_DATE")
    val endDate: String = "",
    @SerialName("THEMECODE")
    val themeCode: String = "",
    @SerialName("LOT")
    val longitude: String = "",
    @SerialName("LAT")
    val latitude: String = "",
    @SerialName("IS_FREE")
    val isFree: String = "",
    @SerialName("HMPG_ADDR")
    val detailUrl: String = "",
    @SerialName("PRO_TIME")
    val time: String = "",
)
