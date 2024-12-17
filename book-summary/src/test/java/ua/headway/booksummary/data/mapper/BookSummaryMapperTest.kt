package ua.headway.booksummary.data.mapper

import org.junit.Assert.*
import org.junit.Test
import ua.headway.booksummary.data.api.BookSummaryResponse

class BookSummaryMapperTest {
    @Test
    fun `toDomain maps BookSummaryResponse to BookSummaryModel correctly`() {
        val response = BookSummaryResponse(
            id = 1,
            bookCoverUrl = "https://example.com/cover.jpg",
            summaryParts = listOf(
                BookSummaryResponse.SummaryPartResponse("Desc1", "audio1.mp3", "Text1"),
                BookSummaryResponse.SummaryPartResponse("Desc2", "audio2.mp3", "Text2")
            )
        )

        val result = response.toDomain()

        assertEquals(1, result.id)
        assertEquals("https://example.com/cover.jpg", result.bookCoverUrl)
        assertEquals(2, result.summaryParts.size)
        assertEquals("Desc1", result.summaryParts[0].description)
        assertEquals("audio1.mp3", result.summaryParts[0].audioUrl)
        assertEquals("Text1", result.summaryParts[0].text)
    }

    @Test
    fun `toDomain maps empty summaryParts correctly`() {
        val response = BookSummaryResponse(
            id = 1,
            bookCoverUrl = "https://example.com/cover.jpg",
            summaryParts = emptyList()
        )

        val result = response.toDomain()

        assertEquals(1, result.id)
        assertEquals("https://example.com/cover.jpg", result.bookCoverUrl)
        assertTrue(result.summaryParts.isEmpty())
    }

    @Test
    fun `toDomain handles empty strings`() {
        val response = BookSummaryResponse(
            id = 1,
            bookCoverUrl = "",
            summaryParts = listOf(
                BookSummaryResponse.SummaryPartResponse("", "", "")
            )
        )

        val result = response.toDomain()

        assertEquals(1, result.id)
        assertEquals("", result.bookCoverUrl)
        assertEquals(1, result.summaryParts.size)
        assertEquals("", result.summaryParts[0].description)
        assertEquals("", result.summaryParts[0].audioUrl)
        assertEquals("", result.summaryParts[0].text)
    }
}