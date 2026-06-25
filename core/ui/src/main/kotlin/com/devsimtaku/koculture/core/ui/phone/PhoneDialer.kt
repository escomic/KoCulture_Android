package com.devsimtaku.koculture.core.ui.phone

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri

fun Context.openPhoneDialer(phoneNumber: String): Boolean {
    val normalizedPhoneNumber = phoneNumber.trim()
    if (normalizedPhoneNumber.isBlank()) {
        return false
    }

    val intent = Intent(
        Intent.ACTION_DIAL,
        Uri.fromParts("tel", normalizedPhoneNumber, null),
    ).apply {
        if (this@openPhoneDialer !is Activity) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    return runCatching {
        startActivity(intent)
        true
    }.getOrDefault(false)
}
