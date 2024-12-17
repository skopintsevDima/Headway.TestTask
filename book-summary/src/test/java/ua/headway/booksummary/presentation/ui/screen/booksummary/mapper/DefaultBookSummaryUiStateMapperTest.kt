package ua.headway.booksummary.presentation.ui.screen.booksummary.mapper

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import ua.headway.booksummary.domain.model.BookSummaryModel
import ua.headway.booksummary.presentation.ui.screen.booksummary.UiState
import ua.headway.core.presentation.ui.resources.provider.ResourceProvider

@RunWith(RobolectricTestRunner::class)
class DefaultBookSummaryUiStateMapperTest {
    private val mockResourceProvider: ResourceProvider = mockk(relaxed = true)
    private val mapper = DefaultBookSummaryUiStateMapper(mockResourceProvider)

    @Test
    fun `toMediaItems maps UiState_Data to MediaItems correctly`() {
        val data = UiState.Data(
            summaryParts = listOf(
                BookSummaryModel.SummaryPart("Desc1", "https://example.com/audio1.mp3", "Text1"),
                BookSummaryModel.SummaryPart("Desc2", "https://example.com/audio2.mp3", "Text2")
            ),
            bookCoverUrl = "https://example.com/cover.jpg",
            currentPartIndex = 0,
            isPlayerReady = true,
            isAudioPlaying = false,
            currentAudioPositionMs = 0,
            currentAudioDurationMs = 0,
            audioSpeedLevel = 1f,
            isListeningModeEnabled = true
        )

        every { mockResourceProvider.getString(any(), any(), any()) } returns "Key Point 1"

        val result = mapper.toMediaItems(data)

        assertEquals(2, result.size)
        assertEquals("https://example.com/audio1.mp3", result[0].localConfiguration?.uri.toString())
        assertEquals("Key Point 1", result[0].mediaMetadata.title.toString())
        assertEquals("Desc1", result[0].mediaMetadata.description.toString())
        assertEquals("https://example.com/cover.jpg", result[0].mediaMetadata.artworkUri.toString())
    }

    @Test
    fun `toMediaItems handles empty summaryParts`() {
        val data = UiState.Data(
            summaryParts = emptyList(),
            bookCoverUrl = "https://example.com/cover.jpg",
            currentPartIndex = 0,
            isPlayerReady = true,
            isAudioPlaying = false,
            currentAudioPositionMs = 0,
            currentAudioDurationMs = 0,
            audioSpeedLevel = 1f,
            isListeningModeEnabled = true
        )

        val result = mapper.toMediaItems(data)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `toMediaItems handles missing or invalid fields`() {
        val data = UiState.Data(
            summaryParts = listOf(
                BookSummaryModel.SummaryPart("", "", ""),
                BookSummaryModel.SummaryPart("Desc2", "", "Text2")
            ),
            bookCoverUrl = "",
            currentPartIndex = 0,
            isPlayerReady = true,
            isAudioPlaying = false,
            currentAudioPositionMs = 0,
            currentAudioDurationMs = 0,
            audioSpeedLevel = 1f,
            isListeningModeEnabled = true
        )

        every { mockResourceProvider.getString(any(), any(), any()) } returns "Key Point"

        val result = mapper.toMediaItems(data)

        assertEquals(2, result.size)
        assertEquals("Key Point", result[0].mediaMetadata.title)
        assertEquals("", result[0].localConfiguration?.uri.toString())
        assertEquals("Key Point", result[1].mediaMetadata.title.toString())
        assertEquals("", result[1].localConfiguration?.uri.toString())
    }
}