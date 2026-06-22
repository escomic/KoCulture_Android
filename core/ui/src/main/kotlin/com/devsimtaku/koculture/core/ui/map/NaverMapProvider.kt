package com.devsimtaku.koculture.core.ui.map

import android.content.Context
import android.net.Uri

fun Context.getExternalMapDestination(
    coordinate: MapCoordinate,
    label: String? = null,
    provider: MapProvider = MapProvider.Naver,
): ExternalMapDestination {
    return when (provider) {
        MapProvider.Naver -> coordinate.toNaverExternalMapDestination(
            appName = packageName,
            label = label,
        )
    }
}

private fun MapCoordinate.toNaverExternalMapDestination(
    appName: String,
    label: String?,
): ExternalMapDestination {
    val placeName = label
        ?.trim()
        ?.takeIf { it.isNotBlank() }
        ?: "위치"
    val appUri = Uri.Builder()
        .scheme("nmap")
        .authority("place")
        .appendQueryParameter("lat", latitude.toString())
        .appendQueryParameter("lng", longitude.toString())
        .appendQueryParameter("name", placeName)
        .appendQueryParameter("appname", appName)
        .build()
    val webUri = Uri.Builder()
        .scheme("https")
        .authority("map.naver.com")
        .path("v5/")
        .appendQueryParameter(
            "c",
            "$longitude,$latitude,15,0,0,0,dh",
        )
        .build()

    return ExternalMapDestination(
        appUri = appUri,
        webUri = webUri,
        appPackageName = "com.nhn.android.nmap",
    )
}
