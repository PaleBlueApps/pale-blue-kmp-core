package com.paleblueapps.kmpcore.formatters.number

import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle
import platform.Foundation.currentLocale
import platform.Foundation.NSLocale
import platform.Foundation.NSNumber


internal object IosNumberFormatter: NumberFormatter {
    override fun format(value: Double, localeCode: String?): String {
        val numberFormat = NSNumberFormatter().apply {
            numberStyle = NSNumberFormatterDecimalStyle
            locale = localeCode?.let {
                NSLocale(localeIdentifier = it)
            } ?: NSLocale.currentLocale()
        }

        val decimalNumber = NSNumber(double = value)

        return numberFormat.stringFromNumber(decimalNumber) ?: value.toString()
    }

    override fun format(value: Int, localeCode: String?): String {
        val numberFormat = NSNumberFormatter().apply {
            numberStyle = NSNumberFormatterDecimalStyle
            locale = localeCode?.let {
                NSLocale(localeIdentifier = it)
            } ?: NSLocale.currentLocale()
        }

        val decimalNumber = NSNumber(int = value)

        return numberFormat.stringFromNumber(decimalNumber) ?: value.toString()
    }

    override fun format(value: Long, localeCode: String?): String {
        val numberFormat = NSNumberFormatter().apply {
            numberStyle = NSNumberFormatterDecimalStyle
            locale = localeCode?.let {
                NSLocale(localeIdentifier = it)
            } ?: NSLocale.currentLocale()
        }

        val decimalNumber = NSNumber(long = value)

        return numberFormat.stringFromNumber(decimalNumber) ?: value.toString()
    }

    override fun parseAsDouble(text: String, localeCode: String?): Double? {
        val numberFormat = NSNumberFormatter().apply {
            numberStyle = NSNumberFormatterDecimalStyle
            locale = localeCode?.let {
                NSLocale(localeIdentifier = it)
            } ?: NSLocale.currentLocale()
        }

        return numberFormat.numberFromString(text)?.doubleValue
    }

    override fun parseAsInt(text: String, localeCode: String?): Int? {
        val numberFormat = NSNumberFormatter().apply {
            numberStyle = NSNumberFormatterDecimalStyle
            locale = localeCode?.let {
                NSLocale(localeIdentifier = it)
            } ?: NSLocale.currentLocale()
        }

        return numberFormat.numberFromString(text)?.intValue
    }

    override fun parseAsLong(text: String, localeCode: String?): Long? {
        val numberFormat = NSNumberFormatter().apply {
            numberStyle = NSNumberFormatterDecimalStyle
            locale = localeCode?.let {
                NSLocale(localeIdentifier = it)
            } ?: NSLocale.currentLocale()
        }

        return numberFormat.numberFromString(text)?.longValue
    }
}

actual fun NumberFormatter(): NumberFormatter = IosNumberFormatter