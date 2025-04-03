package com.paleblueapps.kmpcore.formatters.currency

import java.text.NumberFormat
import java.util.Currency

internal class AndroidCurrencyFormatter : CurrencyFormatter {
    override fun format(
        amount: Double,
        currencyCode: String,
        withCurrencySymbol: Boolean,
        minimumFractionDigits: Int,
        maximumFractionDigits: Int,
    ): String {
        val format = NumberFormat.getCurrencyInstance()
        val currency = Currency.getInstance(currencyCode)
        format.currency = currency
        format.maximumFractionDigits = maximumFractionDigits
        format.minimumFractionDigits = minimumFractionDigits

        if (!withCurrencySymbol) {
            format.currency = null
        }
        return format.format(amount)
    }
}

actual fun CurrencyFormatter(): CurrencyFormatter = AndroidCurrencyFormatter()