package com.devsimtaku.koculture.core.ui.phone

data class PhoneNumberMatch(
    val number: String,
    val range: IntRange,
)

private val PHONE_NUMBER_REGEX =
    Regex("""(?<![0-9])(?<![0-9]-)(?:[0-9]{2,}-)?[0-9]{3,}-[0-9]{4}""")

fun String.extractPhoneNumbers(): List<PhoneNumberMatch> {
    return PHONE_NUMBER_REGEX
        .findAll(this)
        .map { matchResult ->
            PhoneNumberMatch(
                number = matchResult.value,
                range = matchResult.range,
            )
        }
        .toList()
}
