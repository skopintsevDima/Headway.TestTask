package ua.headway.booksummary.presentation.ui.screen.booksummary

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ua.headway.booksummary.domain.model.BookSummaryModel
import ua.headway.booksummary.presentation.ui.screen.booksummary.TestTags.TAG_DATA_SCREEN
import ua.headway.booksummary.presentation.ui.screen.booksummary.TestTags.TAG_DATA_SCREEN_PLACEHOLDER
import ua.headway.booksummary.presentation.ui.screen.booksummary.TestTags.TAG_LOADING_SCREEN
import ua.headway.booksummary.presentation.ui.screen.booksummary.TestTags.TAG_MESSAGE_SCREEN
import ua.headway.booksummary.presentation.ui.screen.booksummary.TestTags.TAG_RETRY_BUTTON
import ua.headway.booksummary.presentation.ui.screen.booksummary.composable.MessageScreen
import ua.headway.booksummary.presentation.ui.screen.booksummary.mock.MockBookSummaryViewModelWithState

@RunWith(AndroidJUnit4::class)
class BookSummaryScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testIdleState() {
        val uiState = UiState.Idle
        val viewModel = MockBookSummaryViewModelWithState(uiState)

        composeTestRule.setContent {
            BookSummaryScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag(TAG_MESSAGE_SCREEN).assertIsDisplayed()
    }

    @Test
    fun testLoadingState() {
        val uiState = UiState.Loading
        val viewModel = MockBookSummaryViewModelWithState(uiState)

        composeTestRule.setContent {
            BookSummaryScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag(TAG_LOADING_SCREEN).assertIsDisplayed()
    }

    @Test
    fun testErrorState() {
        val errorState = UiState.Error.LoadBookDataError("Network Error")
        val viewModel = MockBookSummaryViewModelWithState(errorState)

        composeTestRule.setContent {
            BookSummaryScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag(TAG_DATA_SCREEN_PLACEHOLDER).assertIsDisplayed()
    }

    @Test
    fun testDataState() {
        val dataState = UiState.Data(
            summaryParts = listOf(
                BookSummaryModel.SummaryPart("Desc1", "https://www2.cs.uic.edu/~i101/SoundFiles/BabyElephantWalk60.wav", "Text1"),
                BookSummaryModel.SummaryPart("Desc2", "https://www2.cs.uic.edu/~i101/SoundFiles/BabyElephantWalk60.wav", "Text2")
            ),
            bookCoverUrl = "https://picsum.photos/id/24/1080/1920",
            currentPartIndex = 0,
            isPlayerReady = true,
            isAudioPlaying = false,
            currentAudioPositionMs = 0,
            currentAudioDurationMs = 0,
            audioSpeedLevel = 1f,
            isListeningModeEnabled = true
        )
        val viewModel = MockBookSummaryViewModelWithState(dataState)

        composeTestRule.setContent {
            BookSummaryScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag(TAG_DATA_SCREEN).assertIsDisplayed()
    }

    @Test
    fun testEmptySummaryParts() {
        val emptyData = UiState.Data(
            summaryParts = emptyList(),
            bookCoverUrl = "https://picsum.photos/id/24/1080/1920",
            currentPartIndex = 0,
            isPlayerReady = true,
            isAudioPlaying = false,
            currentAudioPositionMs = 0,
            currentAudioDurationMs = 0,
            audioSpeedLevel = 1f,
            isListeningModeEnabled = true
        )

        val viewModel = MockBookSummaryViewModelWithState(emptyData)

        composeTestRule.setContent {
            BookSummaryScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag(TAG_RETRY_BUTTON).assertIsDisplayed()
    }

    @Test
    fun testRetryButtonTriggersFetchBookSummary() {
        val errorState = UiState.Error.LoadBookDataError("Network Error")
        val viewModel = mockk<BookSummaryViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(errorState)

        composeTestRule.setContent {
            BookSummaryScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag(TAG_RETRY_BUTTON).run {
            assertIsDisplayed()
            performClick()
        }

        verify { viewModel.tryHandleIntent(UiIntent.FetchBookSummary) }
    }

    @Test
    fun testMessageScreenDisplaysMessage() {
        val message = "This is a test message"
        composeTestRule.setContent {
            MessageScreen(message = message)
        }

        composeTestRule.onNodeWithText(message).assertIsDisplayed()
    }

    @Test
    fun testErrorSnackbar() {
        val errorMsg = "UnknownError"
        val errorState = UiState.Error.UnknownError(errorMsg)
        val viewModel = MockBookSummaryViewModelWithState(errorState)

        composeTestRule.setContent {
            BookSummaryScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText(errorMsg).assertIsDisplayed()
    }
}