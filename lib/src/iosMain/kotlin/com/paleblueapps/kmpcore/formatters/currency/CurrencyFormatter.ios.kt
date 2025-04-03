package com.paleblueapps.kmpcore.formatters.currency

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle
import platform.Foundation.currentLocale

internal class IOSCurrencyFormatter : CurrencyFormatter {
    override fun format(
        amount: Double,
        currencyCode: String,
        withCurrencySymbol: Boolean,
        minimumFractionDigits: Int,
        maximumFractionDigits: Int,
    ): String {
        val formatter = NSNumberFormatter()
        formatter.numberStyle = NSNumberFormatterCurrencyStyle
        formatter.currencyCode = currencyCode
        formatter.locale = platform.Foundation.NSLocale.currentLocale()
        formatter.maximumFractionDigits = maximumFractionDigits.toULong()
        formatter.minimumFractionDigits = minimumFractionDigits.toULong()

        val decimalNumber = NSNumber(amount)
        if (!withCurrencySymbol) {
            formatter.currencySymbol = ""
        }
        val formattedString = formatter.stringFromNumber(decimalNumber) ?: "$amount"
        return formattedString.replace(" ", "")
    }
}

actual fun CurrencyFormatter(): CurrencyFormatter = IOSCurrencyFormatter()