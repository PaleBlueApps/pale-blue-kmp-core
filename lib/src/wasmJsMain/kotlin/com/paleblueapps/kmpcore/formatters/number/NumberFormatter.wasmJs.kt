@file:OptIn(ExperimentalWasmJsInterop::class)

package com.paleblueapps.kmpcore.formatters.number

@JsFun("(value, locale) => new Intl.NumberFormat(locale).format(value)")
private external fun jsFormatNumber(value: Double, locale: String): String

@JsFun("(locale) => new Intl.NumberFormat(locale).formatToParts(1234567.89)")
private external fun jsGetNumberParts(locale: String): JsArray<JsAny>

@JsFun("(part) => part.type")
private external fun jsPartType(part: JsAny): String

@JsFun("(part) => part.value")
private external fun jsPartValue(part: JsAny): String

@JsFun("() => navigator.language || 'en-US'")
private external fun jsDefaultLocale(): String

actual fun NumberFormatter(): NumberFormatter = object : NumberFormatter {

    override fun format(value: Double, localeCode: String?): String =
        jsFormatNumber(value, localeCode ?: jsDefaultLocale())

    override fun format(value: Int, localeCode: String?): String =
        jsFormatNumber(value.toDouble(), localeCode ?: jsDefaultLocale())

    override fun format(value: Long, localeCode: String?): String =
        jsFormatNumber(value.toDouble(), localeCode ?: jsDefaultLocale())

    override fun parseAsDouble(text: String, localeCode: String?): Double? =
        parseNumber(text, localeCode)

    override fun parseAsInt(text: String, localeCode: String?): Int? =
        parseNumber(text, localeCode)?.let {
            if (it >= Int.MIN_VALUE && it <= Int.MAX_VALUE) it.toInt() else null
        }

    override fun parseAsLong(text: String, localeCode: String?): Long? =
        parseNumber(text, localeCode)?.let {
            if (it >= Long.MIN_VALUE.toDouble() && it <= Long.MAX_VALUE.toDouble()) it.toLong() else null
        }
}

private fun parseNumber(text: String, localeCode: String?): Double? {
    val locale = localeCode ?: jsDefaultLocale()
    val parts = jsGetNumberParts(locale)

    var decimalSep = "."
    var groupSep = ","

    for (i in 0 until parts.length) {
        val part = parts[i] ?: continue
        when (jsPartType(part)) {
            "decimal" -> decimalSep = jsPartValue(part)
            "group"   -> groupSep   = jsPartValue(part)
        }
    }

    return text
        .replace(groupSep, "")
        .replace(decimalSep, ".")
        .toDoubleOrNull()
}