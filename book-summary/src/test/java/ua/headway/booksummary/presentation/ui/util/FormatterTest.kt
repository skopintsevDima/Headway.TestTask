package ua.headway.booksummary.presentation.ui.util

import org.junit.Assert.*
import org.junit.Test

class FormatterTest {
    @Test
    fun `formatTime formats typical values correctly`() {
        val result = formatTime(65000f)
        assertEquals("01:05", result)
    }

    @Test
    fun `formatTime returns 00 00 for zero milliseconds`() {
        val result = formatTime(0f)
        assertEquals("00:00", result)
    }

    @Test
    fun `formatTime handles values less than a minute`() {
        val result = formatTime(59999f)
        assertEquals("00:59", result)
    }

    @Test
    fun `formatTime handles exact minute boundary`() {
        val result = formatTime(60000f)
        assertEquals("01:00", result)
    }

    @Test
    fun `formatTime handles large values correctly`() {
        val result = formatTime(360000f)
        assertEquals("06:00", result)
    }

    @Test
    fun `formatTime rounds fractional milliseconds correctly`() {
        val result = formatTime(62500f) // 62.5 seconds
        assertEquals("01:02", result)
    }

    @Test
    fun `formatTime handles negative values by returning 00 00`() {
        val result = formatTime(-1000f)
        assertEquals("00:00", result)
    }
}