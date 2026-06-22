package com.devsimtaku.koculture.core.ui.map

import android.net.Uri

data class ExternalMapDestination(
    val appUri: Uri,
    val webUri: Uri,
    val appPackageName: String? = null,
)
