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
        val format = if (withCurrencySymbol) {
            NumberFormat.getCurrencyInstance().also { f ->
                f.currency = Currency.getInstance(currencyCode)
            }
        } else {
            NumberFormat.getNumberInstance()
        }

        format.minimumFractionDigits = minimumFractionDigits
        format.maximumFractionDigits = maximumFractionDigits

        return format.format(amount)
    }
}

actual fun CurrencyFormatter(): CurrencyFormatter = AndroidCurrencyFormatter()