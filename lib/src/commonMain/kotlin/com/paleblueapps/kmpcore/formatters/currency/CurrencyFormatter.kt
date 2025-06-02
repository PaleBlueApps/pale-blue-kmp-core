package com.paleblueapps.kmpcore.formatters.currency

interface CurrencyFormatter {

    /**
     * Formats a monetary amount into a string representation.
     *
     * @param amount The monetary amount to format.
     * @param currencyCode The ISO 4217 currency code (e.g., "USD", "EUR").
     * @param withCurrencySymbol Whether to include the currency symbol (e.g., $, €) in the formatted string.
     * @param minimumFractionDigits The minimum number of fractional digits to display.  Must be non-negative.
     * @param maximumFractionDigits The maximum number of fractional digits to display.  Must be greater than or equal to `minimumFractionDigits`.
     * @return The formatted string representation of the monetary amount.
     *         The format is locale-dependent but generally follows the pattern:  "[Currency Symbol][Amount]".
     *         If `withCurrencySymbol` is false, the currency symbol is omitted.
     *         The amount is formatted with the specified number of fractional digits and the appropriate grouping separators for the locale.
     *
     *
     *   ## Usage example
     * ```
     * val currencyFormatter = CurrencyFormatter()
     * val formattedAmount = currencyFormatter.format(
     *     amount = 1234.56,
     *     currencyCode = "USD",
     *     withCurrencySymbol = true,
     *     minimumFractionDigits = 2,
     *     maximumFractionDigits = 2
     * ) // "$1,234.56"
     * ```
     */
    fun format(
        amount: Double,
        currencyCode: String,
        withCurrencySymbol: Boolean,
        minimumFractionDigits: Int,
        maximumFractionDigits: Int,
    ): String
}

expect fun CurrencyFormatter(): CurrencyFormatter