package com.devsimtaku.koculture.core.ui.map

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri

fun Context.openExternalMap(destination: ExternalMapDestination): Boolean {
    return openViewIntent(
        uri = destination.appUri,
        packageName = destination.appPackageName,
    ) || openViewIntent(uri = destination.webUri)
}

private fun Context.openViewIntent(
    uri: Uri,
    packageName: String? = null,
): Boolean {
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        packageName?.let(::setPackage)
        if (this@openViewIntent !is Activity) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    return runCatching {
        startActivity(intent)
        true
    }.getOrDefault(false)
}
