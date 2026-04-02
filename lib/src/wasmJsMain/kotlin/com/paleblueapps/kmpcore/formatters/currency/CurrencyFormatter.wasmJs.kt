@file:OptIn(ExperimentalWasmJsInterop::class)

package com.paleblueapps.kmpcore.formatters.currency

@JsFun("""
    (amount, currencyCode, withSymbol, minFraction, maxFraction, locale) => {
        const style = withSymbol ? 'currency' : 'decimal';
        const options = {
            style,
            minimumFractionDigits: minFraction,
            maximumFractionDigits: maxFraction,
            ...(withSymbol && { currency: currencyCode })
        };
        return new Intl.NumberFormat(locale, options).format(amount);
    }
""")
private external fun jsFormatCurrency(
    amount: Double,
    currencyCode: String,
    withSymbol: Boolean,
    minFraction: Int,
    maxFraction: Int,
    locale: String
): String

@JsFun("() => navigator.language || 'en-US'")
private external fun jsDefaultLocale(): String

internal object WasmJsCurrencyFormatter : CurrencyFormatter {
    override fun format(
        amount: Double,
        currencyCode: String,
        withCurrencySymbol: Boolean,
        minimumFractionDigits: Int,
        maximumFractionDigits: Int
    ): String = jsFormatCurrency(
        amount,
        currencyCode,
        withCurrencySymbol,
        minimumFractionDigits,
        maximumFractionDigits,
        jsDefaultLocale()
    )
}

actual fun CurrencyFormatter(): CurrencyFormatter = WasmJsCurrencyFormatter
