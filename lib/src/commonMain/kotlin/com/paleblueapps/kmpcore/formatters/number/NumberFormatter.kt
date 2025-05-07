package com.paleblueapps.kmpcore.formatters.number

interface NumberFormatter {
    fun format(value: Double, localeCode: String? = null): String
    fun format(value: Int, localeCode: String? = null): String
    fun format(value: Long, localeCode: String? = null): String
    fun parseAsDouble(text: String, localeCode: String? = null): Double?
    fun parseAsInt(text: String, localeCode: String? = null): Int?
    fun parseAsLong(text: String, localeCode: String? = null): Long?
}

expect fun NumberFormatter(): NumberFormatter