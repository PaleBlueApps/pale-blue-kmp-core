package com.paleblueapps.kmpcore.formatters.number

interface NumberFormatter {
    /**
     * Formats the given value using the specified locale code.
     * @param value The value to format.
     * @param localeCode The locale code to use for formatting. If null, the default locale is used.
     * @return The formatted value as a string.
     *
     * ## Usage Example
     *
     * ```
     * val formatter = NumberFormatter()
     * val formattedValue = formatter.format(123456.789, "en-US")
     * println(formattedValue) // Output: "123,456.789"
     * ```
     */
    fun format(value: Double, localeCode: String? = null): String

    /**
     * Formats the given value using the specified locale code.
     * @param value The value to format.
     * @param localeCode The locale code to use for formatting. If null, the default locale is used.
     * @return The formatted value as a string.
     *
     * ## Usage Example
     *
     * ```
     * val formatter = NumberFormatter()
     * val formattedValue = formatter.format(123456, "en-US")
     * println(formattedValue) // Output: "123,456"
     * ```
     */
    fun format(value: Int, localeCode: String? = null): String

    /**
     * Formats the given value using the specified locale code.
     * @param value The value to format.
     * @param localeCode The locale code to use for formatting. If null, the default locale is used.
     * @return The formatted value as a string.
     *
     * ## Usage Example
     *
     * ```
     * val formatter = NumberFormatter()
     * val formattedValue = formatter.format(123456789, "en-US")
     * println(formattedValue) // Output: "123,456,789"
     * ```
     */
    fun format(value: Long, localeCode: String? = null): String

    /**
     * Parses the given text as a double using the specified locale code.
     * @param text The text to parse.
     * @param localeCode The locale code to use for parsing. If null, the default locale is used.
     * @return The parsed value as a double, or null if parsing failed.
     */
    fun parseAsDouble(text: String, localeCode: String? = null): Double?

    /**
     * Parses the given text as a double using the specified locale code.
     * @param text The text to parse.
     * @param localeCode The locale code to use for parsing. If null, the default locale is used.
     * @return The parsed value as a double, or null if parsing failed.
     */
    fun parseAsInt(text: String, localeCode: String? = null): Int?

    /**
     * Parses the given text as a double using the specified locale code.
     * @param text The text to parse.
     * @param localeCode The locale code to use for parsing. If null, the default locale is used.
     * @return The parsed value as a double, or null if parsing failed.
     */
    fun parseAsLong(text: String, localeCode: String? = null): Long?
}

expect fun NumberFormatter(): NumberFormatter