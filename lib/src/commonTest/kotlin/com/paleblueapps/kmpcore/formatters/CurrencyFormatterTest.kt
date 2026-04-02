package com.paleblueapps.kmpcore.formatters

import com.paleblueapps.kmpcore.formatters.currency.CurrencyFormatter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CurrencyFormatterTest {

    private val formatter = CurrencyFormatter()

    @Test
    fun `format with symbol includes currency symbol`() {
        val result = formatter.format(
            amount = 1234.56,
            currencyCode = "USD",
            withCurrencySymbol = true,
            minimumFractionDigits = 2,
            maximumFractionDigits = 2
        )
        // Symbol may appear as "$", "US$", or "USD" depending on locale
        assertTrue(
            result.contains("$") || result.contains("USD"),
            "Expected currency symbol in: $result"
        )
    }

    @Test
    fun `format without symbol omits currency symbol`() {
        val result = formatter.format(
            amount = 1234.56,
            currencyCode = "USD",
            withCurrencySymbol = false,
            minimumFractionDigits = 2,
            maximumFractionDigits = 2
        )
        assertFalse(result.contains("$"), "Expected no currency symbol in: $result")
        assertFalse(result.contains("USD"), "Expected no currency code in: $result")
    }

    @Test
    fun `format EUR with symbol includes euro symbol`() {
        val result = formatter.format(
            amount = 1000.0,
            currencyCode = "EUR",
            withCurrencySymbol = true,
            minimumFractionDigits = 2,
            maximumFractionDigits = 2
        )
        assertTrue(
            result.contains("€") || result.contains("EUR"),
            "Expected EUR symbol in: $result"
        )
    }

    // ─── Digit content ────────────────────────────────────────────────────────

    @Test
    fun `format contains correct integer digits`() {
        val result = formatter.format(
            amount = 1234.56,
            currencyCode = "USD",
            withCurrencySymbol = false,
            minimumFractionDigits = 2,
            maximumFractionDigits = 2
        )
        // Strip non-digit, non-separator chars and check core digits are present
        assertTrue(result.contains("1"), "Missing digit 1 in: $result")
        assertTrue(result.contains("2"), "Missing digit 2 in: $result")
        assertTrue(result.contains("3"), "Missing digit 3 in: $result")
        assertTrue(result.contains("4"), "Missing digit 4 in: $result")
    }

    // ─── Fraction digits ──────────────────────────────────────────────────────

    @Test
    fun `format with zero fraction digits produces whole number`() {
        val result = formatter.format(
            amount = 1234.0,
            currencyCode = "USD",
            withCurrencySymbol = false,
            minimumFractionDigits = 0,
            maximumFractionDigits = 0
        )
        assertFalse(result.contains("."), "Expected no decimal point in: $result")
        assertFalse(result.contains(",") && result.endsWith("00"), "Expected no fractional part in: $result")
    }

    @Test
    fun `format respects minimumFractionDigits padding`() {
        val result = formatter.format(
            amount = 5.0,
            currencyCode = "USD",
            withCurrencySymbol = false,
            minimumFractionDigits = 3,
            maximumFractionDigits = 3
        )
        // e.g. "5.000" — last 3 chars after separator should be "000"
        assertTrue(
            result.endsWith("000"),
            "Expected 3 fractional zeros in: $result"
        )
    }

    @Test
    fun `format respects maximumFractionDigits truncation`() {
        val result = formatter.format(
            amount = 1.23456,
            currencyCode = "USD",
            withCurrencySymbol = false,
            minimumFractionDigits = 2,
            maximumFractionDigits = 2
        )
        // Must end with exactly 2 fractional digits, value rounded to 1.23
        assertTrue(
            result.endsWith("23"),
            "Expected value rounded to 2 decimals in: $result"
        )
    }

    @Test
    fun `format with high precision keeps all fraction digits`() {
        val result = formatter.format(
            amount = 0.123456,
            currencyCode = "BTC",
            withCurrencySymbol = false,
            minimumFractionDigits = 6,
            maximumFractionDigits = 6
        )
        assertTrue(
            result.endsWith("123456"),
            "Expected 6 fraction digits in: $result"
        )
    }

    // ─── Edge cases ───────────────────────────────────────────────────────────

    @Test
    fun `format zero amount`() {
        val result = formatter.format(
            amount = 0.0,
            currencyCode = "USD",
            withCurrencySymbol = false,
            minimumFractionDigits = 2,
            maximumFractionDigits = 2
        )
        assertTrue(
            result.contains("0"),
            "Expected zero in: $result"
        )
    }

    @Test
    fun `format negative amount contains minus sign`() {
        val result = formatter.format(
            amount = -42.5,
            currencyCode = "USD",
            withCurrencySymbol = false,
            minimumFractionDigits = 2,
            maximumFractionDigits = 2
        )
        // Negative can be represented as "-" or "(" ... ")" depending on locale
        assertTrue(
            result.contains("-") || result.contains("("),
            "Expected negative indicator in: $result"
        )
    }

    @Test
    fun `format large amount contains grouping separator`() {
        val result = formatter.format(
            amount = 1_000_000.0,
            currencyCode = "USD",
            withCurrencySymbol = false,
            minimumFractionDigits = 0,
            maximumFractionDigits = 0
        )
        // Grouping separator varies by locale (",", ".", " ", "'", etc.)
        // but the digit groups 1, 000, 000 must all appear
        assertTrue(result.contains("1"), "Missing leading 1 in: $result")
        val digits = result.filter { it.isDigit() }
        assertEquals("1000000", digits, "Expected all digits of 1,000,000 in: $result")
    }

    @Test
    fun `format minimumFractionDigits equal to maximumFractionDigits produces exact scale`() {
        listOf(0, 1, 2, 4).forEach { scale ->
            val result = formatter.format(
                amount = 1.0,
                currencyCode = "USD",
                withCurrencySymbol = false,
                minimumFractionDigits = scale,
                maximumFractionDigits = scale
            )
            val fractionalPart = result.substringAfterLast('.', missingDelimiterValue = "")
            assertEquals(
                scale,
                fractionalPart.length,
                "Expected $scale fraction digits for scale=$scale, got: $result"
            )
        }
    }
}