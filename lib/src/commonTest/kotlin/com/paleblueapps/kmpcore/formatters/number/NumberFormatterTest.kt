package com.paleblueapps.kmpcore.formatters.number

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NumberFormatterTest {

    private val formatter = NumberFormatter()

    // ─── format(Double) ───────────────────────────────────────────────────────

    @Test
    fun `format double with en-US locale uses dot as decimal separator`() {
        val result = formatter.format(1234.56, "en-US")
        assertTrue(result.contains("."), "Expected dot decimal separator in: $result")
    }

    @Test
    fun `format double with en-US locale uses comma as grouping separator`() {
        val result = formatter.format(1234567.89, "en-US")
        assertEquals("1,234,567.89", result, "Unexpected format for en-US double")
    }

    @Test
    fun `format double with de-DE locale uses comma as decimal separator`() {
        val result = formatter.format(1234.56, "de-DE")
        assertTrue(result.contains(","), "Expected comma decimal separator in: $result")
    }

    @Test
    fun `format double zero`() {
        val result = formatter.format(0.0, "en-US")
        assertTrue(result.contains("0"), "Expected 0 in: $result")
    }

    @Test
    fun `format negative double contains minus sign`() {
        val result = formatter.format(-1234.56, "en-US")
        assertTrue(
            result.contains("-") || result.contains("("),
            "Expected negative indicator in: $result"
        )
    }

    @Test
    fun `format double with null locale returns non-empty string`() {
        val result = formatter.format(42.5, null)
        assertTrue(result.isNotEmpty(), "Expected non-empty result for null locale")
        assertTrue(result.contains("4") && result.contains("2"), "Expected digits in: $result")
    }

    @Test
    fun `format double preserves all digits`() {
        val result = formatter.format(1234567.89, "en-US")
        val digits = result.filter { it.isDigit() }
        assertEquals("123456789", digits, "Expected all digits in: $result")
    }

    // ─── format(Int) ──────────────────────────────────────────────────────────

    @Test
    fun `format int with en-US locale`() {
        val result = formatter.format(123456, "en-US")
        assertEquals("123,456", result, "Unexpected format for en-US int")
    }

    @Test
    fun `format int zero`() {
        val result = formatter.format(0, "en-US")
        assertEquals("0", result, "Expected 0 for zero int")
    }

    @Test
    fun `format negative int contains minus sign`() {
        val result = formatter.format(-999, "en-US")
        assertTrue(
            result.contains("-") || result.contains("("),
            "Expected negative indicator in: $result"
        )
    }

    @Test
    fun `format int with null locale returns non-empty string`() {
        val result = formatter.format(100, null)
        assertTrue(result.isNotEmpty())
        assertTrue(result.contains("1") && result.contains("0"))
    }

    @Test
    fun `format large int preserves all digits`() {
        val result = formatter.format(1000000, "en-US")
        val digits = result.filter { it.isDigit() }
        assertEquals("1000000", digits, "Expected all digits of 1,000,000 in: $result")
    }

    // ─── format(Long) ─────────────────────────────────────────────────────────

    @Test
    fun `format long with en-US locale`() {
        val result = formatter.format(123456789L, "en-US")
        assertEquals("123,456,789", result, "Unexpected format for en-US long")
    }

    @Test
    fun `format long zero`() {
        val result = formatter.format(0L, "en-US")
        assertEquals("0", result, "Expected 0 for zero long")
    }

    @Test
    fun `format negative long contains minus sign`() {
        val result = formatter.format(-1_000_000L, "en-US")
        assertTrue(
            result.contains("-") || result.contains("("),
            "Expected negative indicator in: $result"
        )
    }

    @Test
    fun `format long max value does not throw`() {
        val result = formatter.format(Long.MAX_VALUE, "en-US")
        assertTrue(result.isNotEmpty(), "Expected non-empty result for Long.MAX_VALUE")
        assertTrue(
            result.first().isDigit() || result.first() == '-',
            "Expected result to start with a digit, got: $result"
        )
    }

    @Test
    fun `format long large value within double precision preserves all digits`() {
        val safeMax = 9_007_199_254_740_992L
        val result = formatter.format(safeMax, "en-US")
        val digits = result.filter { it.isDigit() }
        assertEquals(safeMax.toString(), digits, "Expected exact digits in: $result")
    }

    @Test
    fun `format long with null locale returns non-empty string`() {
        val result = formatter.format(999L, null)
        assertTrue(result.isNotEmpty())
        assertTrue(result.contains("9"))
    }

    // ─── parseAsDouble ────────────────────────────────────────────────────────

    @Test
    fun `parseAsDouble plain decimal string`() {
        val result = formatter.parseAsDouble("1234.56", "en-US")
        assertNotNull(result)
        assertEquals(1234.56, result, 0.0001)
    }

    @Test
    fun `parseAsDouble grouped string en-US`() {
        val result = formatter.parseAsDouble("1,234,567.89", "en-US")
        assertNotNull(result)
        assertEquals(1234567.89, result, 0.0001)
    }

    @Test
    fun `parseAsDouble de-DE grouped string`() {
        val result = formatter.parseAsDouble("1.234,56", "de-DE")
        assertNotNull(result)
        assertEquals(1234.56, result, 0.0001)
    }

    @Test
    fun `parseAsDouble zero`() {
        val result = formatter.parseAsDouble("0", "en-US")
        assertNotNull(result)
        assertEquals(0.0, result, 0.0)
    }

    @Test
    fun `parseAsDouble negative value`() {
        val result = formatter.parseAsDouble("-42.5", "en-US")
        assertNotNull(result)
        assertEquals(-42.5, result, 0.0001)
    }

    @Test
    fun `parseAsDouble invalid text returns null`() {
        val result = formatter.parseAsDouble("not_a_number", "en-US")
        assertNull(result, "Expected null for invalid input")
    }

    @Test
    fun `parseAsDouble empty string returns null`() {
        val result = formatter.parseAsDouble("", "en-US")
        assertNull(result, "Expected null for empty input")
    }

    @Test
    fun `parseAsDouble roundtrip with format`() {
        val original = 9876.54
        val formatted = formatter.format(original, "en-US")
        val parsed = formatter.parseAsDouble(formatted, "en-US")
        assertNotNull(parsed)
        assertEquals(original, parsed, 0.0001)
    }

    // ─── parseAsInt ───────────────────────────────────────────────────────────

    @Test
    fun `parseAsInt plain integer string`() {
        val result = formatter.parseAsInt("42", "en-US")
        assertNotNull(result)
        assertEquals(42, result)
    }

    @Test
    fun `parseAsInt grouped string en-US`() {
        val result = formatter.parseAsInt("1,000,000", "en-US")
        assertNotNull(result)
        assertEquals(1_000_000, result)
    }

    @Test
    fun `parseAsInt negative value`() {
        val result = formatter.parseAsInt("-100", "en-US")
        assertNotNull(result)
        assertEquals(-100, result)
    }

    @Test
    fun `parseAsInt value exceeding Int range returns null`() {
        val overflowValue = (Int.MAX_VALUE.toLong() + 1).toString()
        val result = formatter.parseAsInt(overflowValue, "en-US")
        assertNull(result, "Expected null for value exceeding Int range")
    }

    @Test
    fun `parseAsInt invalid text returns null`() {
        val result = formatter.parseAsInt("abc", "en-US")
        assertNull(result, "Expected null for invalid input")
    }

    @Test
    fun `parseAsInt empty string returns null`() {
        val result = formatter.parseAsInt("", "en-US")
        assertNull(result, "Expected null for empty input")
    }

    @Test
    fun `parseAsInt roundtrip with format`() {
        val original = 123456
        val formatted = formatter.format(original, "en-US")
        val parsed = formatter.parseAsInt(formatted, "en-US")
        assertNotNull(parsed)
        assertEquals(original, parsed)
    }

    // ─── parseAsLong ──────────────────────────────────────────────────────────

    @Test
    fun `parseAsLong plain long string`() {
        val result = formatter.parseAsLong("123456789", "en-US")
        assertNotNull(result)
        assertEquals(123456789L, result)
    }

    @Test
    fun `parseAsLong grouped string en-US`() {
        val result = formatter.parseAsLong("1,000,000,000", "en-US")
        assertNotNull(result)
        assertEquals(1_000_000_000L, result)
    }

    @Test
    fun `parseAsLong negative value`() {
        val result = formatter.parseAsLong("-999999", "en-US")
        assertNotNull(result)
        assertEquals(-999999L, result)
    }

    @Test
    fun `parseAsLong invalid text returns null`() {
        val result = formatter.parseAsLong("xyz", "en-US")
        assertNull(result, "Expected null for invalid input")
    }

    @Test
    fun `parseAsLong empty string returns null`() {
        val result = formatter.parseAsLong("", "en-US")
        assertNull(result, "Expected null for empty input")
    }

    @Test
    fun `parseAsLong roundtrip with format`() {
        val original = 9_876_543_210L
        val formatted = formatter.format(original, "en-US")
        val parsed = formatter.parseAsLong(formatted, "en-US")
        assertNotNull(parsed)
        assertEquals(original, parsed)
    }
}