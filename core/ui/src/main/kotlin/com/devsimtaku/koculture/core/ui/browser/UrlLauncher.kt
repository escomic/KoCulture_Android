package com.devsimtaku.koculture.core.ui.browser

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

fun Context.openUrl(url: String): Boolean {
    val uri = url.toNormalizedUri() ?: return false

    return openWithCustomTabs(uri) || openWithViewIntent(uri)
}

private fun String.toNormalizedUri(): Uri? {
    val trimmedUrl = trim()

    if (trimmedUrl.isBlank()) {
        return null
    }

    return trimmedUrl
        .takeIf {
            it.startsWith(prefix = "http://", ignoreCase = true) ||
                it.startsWith(prefix = "https://", ignoreCase = true)
        }
        ?.toUri()
        ?: "https://$trimmedUrl".toUri()
}

private fun Context.openWithCustomTabs(uri: Uri): Boolean {
    return runCatching {
        CustomTabsIntent.Builder()
            .build()
            .launchUrl(this, uri)
        true
    }.getOrDefault(false)
}

private fun Context.openWithViewIntent(uri: Uri): Boolean {
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        if (this@openWithViewIntent !is Activity) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    return runCatching {
        startActivity(intent)
        true
    }.getOrDefault(false)
}
