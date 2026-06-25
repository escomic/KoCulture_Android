package com.devsimtaku.koculture.feature.eventdetail

private val DATE_PREFIX_REGEX = Regex("""^\d{4}-\d{2}-\d{2}(?=\s|T|$)""")

internal fun formatDateRange(
    startDate: String,
    endDate: String,
    fallbackDate: String,
): String {
    return listOf(startDate, endDate)
        .map(String::toDisplayDate)
        .filter(String::isNotBlank)
        .distinct()
        .joinToString(separator = " ~ ")
        .ifBlank { fallbackDate.trim() }
}

private fun String.toDisplayDate(): String {
    val trimmedDate = trim()
    return DATE_PREFIX_REGEX.find(trimmedDate)?.value ?: trimmedDate
}
