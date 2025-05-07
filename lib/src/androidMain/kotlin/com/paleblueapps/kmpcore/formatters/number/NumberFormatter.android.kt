package com.paleblueapps.kmpcore.formatters.number

import java.text.NumberFormat
import java.util.Locale

internal object AndroidNumberFormatter: NumberFormatter {

    override fun format(value: Double, localeCode: String?): String {
        val numberFormat = localeCode?.let {
            NumberFormat.getNumberInstance(Locale.forLanguageTag(it))
        } ?: NumberFormat.getNumberInstance()

        return numberFormat.format(value)
    }

    override fun format(value: Int, localeCode: String?): String {
        val numberFormat = localeCode?.let {
            NumberFormat.getNumberInstance(Locale.forLanguageTag(it))
        } ?: NumberFormat.getNumberInstance()

        return numberFormat.format(value)
    }

    override fun format(value: Long, localeCode: String?): String {
        val numberFormat = localeCode?.let {
            NumberFormat.getNumberInstance(Locale.forLanguageTag(it))
        } ?: NumberFormat.getNumberInstance()

        return numberFormat.format(value)
    }

    override fun parseAsDouble(text: String, localeCode: String?): Double? {
        val numberFormat = localeCode?.let {
            NumberFormat.getNumberInstance(Locale.forLanguageTag(it))
        } ?: NumberFormat.getNumberInstance()

        return try {
            numberFormat.parse(text)?.toDouble()
        } catch (e: Exception) {
            null
        }
    }

    override fun parseAsInt(text: String, localeCode: String?): Int? {
        val numberFormat = localeCode?.let {
            NumberFormat.getNumberInstance(Locale.forLanguageTag(it))
        } ?: NumberFormat.getNumberInstance()

        return try {
            numberFormat.parse(text)?.toInt()
        } catch (e: Exception) {
            null
        }
    }

    override fun parseAsLong(text: String, localeCode: String?): Long? {
        val numberFormat = localeCode?.let {
            NumberFormat.getNumberInstance(Locale.forLanguageTag(it))
        } ?: NumberFormat.getNumberInstance()

        return try {
            numberFormat.parse(text)?.toLong()
        } catch (e: Exception) {
            null
        }
    }
}

actual fun NumberFormatter(): NumberFormatter = AndroidNumberFormatter