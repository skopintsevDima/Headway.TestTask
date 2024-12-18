package ua.headway.booksummary.presentation.ui.screen.booksummary

import android.content.Context
import androidx.media3.common.MediaItem
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import ua.headway.booksummary.domain.interactor.AudioPlaybackInteractor
import ua.headway.booksummary.domain.model.BookSummaryModel
import ua.headway.booksummary.domain.model.BookSummaryModel.SummaryPart
import ua.headway.booksummary.domain.usecase.GetBookSummaryUseCase
import ua.headway.booksummary.presentation.manager.PlayerSetupManager
import ua.headway.booksummary.presentation.ui.screen.booksummary.mapper.BookSummaryUiStateMapper
import ua.headway.booksummary.presentation.util.Constants.ErrorCodes.BookSummary.ERROR_PLAYER_PLAYBACK
import ua.headway.core.presentation.ui.resources.provider.ResourceProvider

@OptIn(ExperimentalCoroutinesApi::class)
class BookSummaryViewModelImplTest {
    private val mockGetBookSummaryUseCase: GetBookSummaryUseCase = mockk()
    private val mockAudioPlaybackInteractor: AudioPlaybackInteractor = mockk(relaxed = true)
    private val mockPlayerSetupManager: PlayerSetupManager = mockk(relaxed = true)
    private val mockUiStateMapper: BookSummaryUiStateMapper = mockk(relaxed = true)
    private val mockResourceProvider: ResourceProvider = mockk(relaxed = true)

    private val testSummaryParts = listOf(
        SummaryPart("desc1", "audioUrl1", "text1"),
        SummaryPart("desc2", "audioUrl2", "text2"),
        SummaryPart("desc3", "audioUrl3", "text3")
    )
    private val testBookSummary = BookSummaryModel(1234L, testSummaryParts, "bookCoverUrl")

    private lateinit var viewModel: BookSummaryViewModel
    private val testCoroutineScope = TestScope()
    private val testDispatcher = StandardTestDispatcher(testCoroutineScope.testScheduler)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = BookSummaryViewModelImpl(
            getBookSummaryUseCase = mockGetBookSummaryUseCase,
            audioPlaybackInteractor = mockAudioPlaybackInteractor,
            playerSetupManager = mockPlayerSetupManager,
            uiStateMapper = mockUiStateMapper,
            resourceProvider = mockResourceProvider,
            backgroundOpsDispatcher = testDispatcher
        )
    }

    @Test
    fun `initial state should be Idle`() = testCoroutineScope.runTest {
        assert(viewModel.uiState.value is UiState.Idle)
    }

    @Test
    fun `InitPlayer initializes player successfully`() = testCoroutineScope.runTest {
        val mockContext = mockk<Context>()
        val mockMediaItems = mockk<List<MediaItem>>()
        coEvery { mockGetBookSummaryUseCase.execute(any()) } returns testBookSummary
        every { mockAudioPlaybackInteractor.isPlayerAvailable } returns false
        every { mockUiStateMapper.toMediaItems(any()) } returns mockMediaItems

        viewModel.tryHandleIntent(UiIntent.FetchBookSummary)
        advanceUntilIdle()

        viewModel.tryHandleIntent(UiIntent.InitPlayer(mockContext))
        advanceUntilIdle()

        verify { mockPlayerSetupManager.setupPlayer(
            mockAudioPlaybackInteractor,
            mockContext,
            mockMediaItems,
            any(),
            any()
        ) }
    }

    @Test
    fun `InitPlayer fails gracefully on error`() = testCoroutineScope.runTest {
        val errorMessage = "Player setup failed"
        val mockContext = mockk<Context>()
        val mockMediaItems = mockk<List<MediaItem>>()

        coEvery { mockGetBookSummaryUseCase.execute(any()) } returns testBookSummary
        every { mockAudioPlaybackInteractor.isPlayerAvailable } returns false
        every { mockUiStateMapper.toMediaItems(any()) } returns mockMediaItems
        every { mockPlayerSetupManager.setupPlayer(any(), any(), any(), any(), captureLambda()) } answers {
            val failureCallback = lastArg<(Throwable) -> Unit>()
            failureCallback(Exception(errorMessage))
        }
        every { mockResourceProvider.getString(any(), any()) } returns errorMessage

        viewModel.tryHandleIntent(UiIntent.FetchBookSummary)
        advanceUntilIdle()

        viewModel.tryHandleIntent(UiIntent.InitPlayer(mockContext))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assert(state is UiState.Error)
        assertEquals(errorMessage, (state as UiState.Error).errorMsg)
    }

    @Test
    fun `InitPlayer handles missing data gracefully`() = testCoroutineScope.runTest {
        viewModel.tryHandleIntent(UiIntent.InitPlayer(mockk()))
        advanceUntilIdle()

        assert(viewModel.uiState.value is UiState.Error)
    }

    @Test
    fun `FetchBookSummary successful data flow`() = testCoroutineScope.runTest {
        coEvery { mockGetBookSummaryUseCase.execute(any()) } returns testBookSummary

        viewModel.tryHandleIntent(UiIntent.FetchBookSummary)
        advanceUntilIdle()

        assert(viewModel.uiState.value is UiState.Data)
        coVerify { mockGetBookSummaryUseCase.execute(any()) }
    }

    @Test
    fun `FetchBookSummary sets Loading and then Data state on success`() = testCoroutineScope.runTest {
        coEvery { mockGetBookSummaryUseCase.execute(any()) } coAnswers {
            delay(TIME_ADVANCE_BY)
            testBookSummary
        }

        viewModel.tryHandleIntent(UiIntent.FetchBookSummary)
        advanceTimeBy(TIME_ADVANCE_BY / 2)
        val state1 = viewModel.uiState.value
        assert(state1 is UiState.Loading)

        advanceTimeBy(TIME_ADVANCE_BY)
        val state2 = viewModel.uiState.value
        assert(state2 is UiState.Data)
        assertEquals(testBookSummary.bookCoverUrl, state2.asData?.bookCoverUrl)
    }

    @Test
    fun `FetchBookSummary sets Loading and then LoadBookDataError state on error`() = testCoroutineScope.runTest {
        val errorMessage = "Load book data failed"
        coEvery { mockGetBookSummaryUseCase.execute(any()) } coAnswers {
            delay(TIME_ADVANCE_BY)
            throw Exception(errorMessage)
        }
        every { mockResourceProvider.getString(any(), any()) } returns errorMessage

        viewModel.tryHandleIntent(UiIntent.FetchBookSummary)
        advanceTimeBy(TIME_ADVANCE_BY / 2)
        val state1 = viewModel.uiState.value
        assert(state1 is UiState.Loading)

        advanceTimeBy(TIME_ADVANCE_BY)
        val state2 = viewModel.uiState.value
        assert(state2 is UiState.Error.LoadBookDataError)
        assertEquals(errorMessage, (state2 as UiState.Error).errorMsg)
    }

    @Test
    fun `FetchBookSummary prevents duplicate fetches`() = testCoroutineScope.runTest {
        coEvery { mockGetBookSummaryUseCase.execute(any()) } returns testBookSummary

        viewModel.tryHandleIntent(UiIntent.FetchBookSummary)
        advanceUntilIdle()
        viewModel.tryHandleIntent(UiIntent.FetchBookSummary)
        advanceUntilIdle()

        coVerify(exactly = 1) { mockGetBookSummaryUseCase.execute(any()) }
    }

    @Test
    fun `FetchBookSummary sets Loading and then NoDataForPlayerError state when data is empty`() = testCoroutineScope.runTest {
        val errorMessage = "No data for player"
        val emptyBookSummary = testBookSummary.copy(summaryParts = emptyList())
        coEvery { mockGetBookSummaryUseCase.execute(any()) } coAnswers {
            delay(TIME_ADVANCE_BY)
            emptyBookSummary
        }
        every { mockResourceProvider.getString(any()) } returns errorMessage

        viewModel.tryHandleIntent(UiIntent.FetchBookSummary)
        advanceTimeBy(TIME_ADVANCE_BY / 2)
        val state1 = viewModel.uiState.value
        assert(state1 is UiState.Loading)

        advanceTimeBy(TIME_ADVANCE_BY)
        val state2 = viewModel.uiState.value
        assert(state2 is UiState.Error.NoDataForPlayerError)
        assertEquals(errorMessage, (state2 as UiState.Error).errorMsg)
    }

    @Test
    fun `ToggleAudio invokes playback interactor`() = testCoroutineScope.runTest {
        coEvery { mockGetBookSummaryUseCase.execute(any()) } returns testBookSummary
        every { mockAudioPlaybackInteractor.isPlayerAvailable } returns true

        viewModel.tryHandleIntent(UiIntent.FetchBookSummary)
        advanceUntilIdle()
        viewModel.tryHandleIntent(UiIntent.ToggleAudio)

        verify { mockAudioPlaybackInteractor.togglePlayback(true) }
    }

    @Test
    fun `ToggleAudio does nothing when no valid state`() = testCoroutineScope.runTest {
        viewModel.tryHandleIntent(UiIntent.ToggleAudio)
        verify(exactly = 0) { mockAudioPlaybackInteractor.togglePlayback(any()) }
    }

    @Test
    fun `ToggleAudio does nothing when player is unavailable`() = testCoroutineScope.runTest {
        coEvery { mockGetBookSummaryUseCase.execute(any()) } returns testBookSummary
        every { mockAudioPlaybackInteractor.isPlayerAvailable } returns false

        viewModel.tryHandleIntent(UiIntent.FetchBookSummary)
        advanceUntilIdle()
        viewModel.tryHandleIntent(UiIntent.ToggleAudio)

        verify(exactly = 0) { mockAudioPlaybackInteractor.togglePlayback(any()) }
    }

    @Test
    fun `ToggleAudioSpeed invokes playback interactor with correct speed level`() = testCoroutineScope.runTest {
        coEvery { mockGetBookSummaryUseCase.execute(any()) } returns testBookSummary
        every { mockAudioPlaybackInteractor.isPlayerAvailable } returns true

        viewModel.tryHandleIntent(UiIntent.FetchBookSummary)
        advanceUntilIdle()

        viewModel.tryHandleIntent(UiIntent.ToggleAudioSpeed)
        verify { mockAudioPlaybackInteractor.changeSpeed(1.5f) }
    }

    @Test
    fun `ToggleAudioSpeed does nothing without valid state`() = testCoroutineScope.runTest {
        viewModel.tryHandleIntent(UiIntent.ToggleAudioSpeed)
        verify(exactly = 0) { mockAudioPlaybackInteractor.changeSpeed(any()) }
    }

    @Test
    fun `ToggleAudioSpeed does nothing when player is unavailable`() = testCoroutineScope.runTest {
        coEvery { mockGetBookSummaryUseCase.execute(any()) } returns testBookSummary
        every { mockAudioPlaybackInteractor.isPlayerAvailable } returns false

        viewModel.tryHandleIntent(UiIntent.FetchBookSummary)
        advanceUntilIdle()
        viewModel.tryHandleIntent(UiIntent.ToggleAudioSpeed)

        verify(exactly = 0) { mockAudioPlaybackInteractor.changeSpeed(any()) }
    }

    @Test
    fun `ShiftAudioPosition adjusts playback position`() = testCoroutineScope.runTest {
        coEvery { mockGetBookSummaryUseCase.execute(any()) } returns testBookSummary
        every { mockAudioPlaybackInteractor.isPlayerAvailable } returns true

        viewModel.tryHandleIntent(UiIntent.FetchBookSummary)
        advanceUntilIdle()
        assert(viewModel.uiState.value is UiState.Data)

        viewModel.tryHandleIntent(UiIntent.ShiftAudioPosition(3000L))
        verify { mockAudioPlaybackInteractor.seekTo(3000L) }
    }

    @Test
    fun `ShiftAudioPosition does nothing when player is unavailable`() = testCoroutineScope.runTest {
        coEvery { mockGetBookSummaryUseCase.execute(any()) } returns testBookSummary
        every { mockAudioPlaybackInteractor.isPlayerAvailable } returns false

        viewModel.tryHandleIntent(UiIntent.FetchBookSummary)
        advanceUntilIdle()
        viewModel.tryHandleIntent(UiIntent.ShiftAudioPosition(3000L))

        verify(exactly = 0) { mockAudioPlaybackInteractor.seekTo(any()) }
    }

    @Test
    fun `FinishPlaybackPositionChange seeks to new position`() = testCoroutineScope.runTest {
        coEvery { mockGetBookSummaryUseCase.execute(any()) } returns testBookSummary
        every { mockAudioPlaybackInteractor.isPlayerAvailable } returns true

        viewModel.tryHandleIntent(UiIntent.FetchBookSummary)
        advanceUntilIdle()

        val newAudioPositionMs = 5000L
        viewModel.tryHandleIntent(UiIntent.FinishPlaybackPositionChange(newAudioPositionMs))
        verify { mockAudioPlaybackInteractor.seekTo(newAudioPositionMs) }
    }

    @Test
    fun `FinishPlaybackPositionChange does nothing when player is unavailable`() = testCoroutineScope.runTest {
        coEvery { mockGetBookSummaryUseCase.execute(any()) } returns testBookSummary
        every { mockAudioPlaybackInteractor.isPlayerAvailable } returns false

        viewModel.tryHandleIntent(UiIntent.FetchBookSummary)
        advanceUntilIdle()
        viewModel.tryHandleIntent(UiIntent.FinishPlaybackPositionChange(3000L))

        verify(exactly = 0) { mockAudioPlaybackInteractor.seekTo(any()) }
    }

    @Test
    fun `GoNextPart skips to the next audio track`() = testCoroutineScope.runTest {
        coEvery { mockGetBookSummaryUseCase.execute(any()) } returns testBookSummary
        every { mockAudioPlaybackInteractor.isPlayerAvailable } returns true

        viewModel.tryHandleIntent(UiIntent.FetchBookSummary)
        advanceUntilIdle()

        viewModel.tryHandleIntent(UiIntent.GoNextPart)
        verify { mockAudioPlaybackInteractor.skipForward() }
    }

    @Test
    fun `GoNextPart does nothing when already at the last part`() = testCoroutineScope.runTest {
        val bookSummaryOnePart = testBookSummary.copy(
            summaryParts = listOf(testSummaryParts.first())
        )
        coEvery { mockGetBookSummaryUseCase.execute(any()) } returns bookSummaryOnePart

        viewModel.tryHandleIntent(UiIntent.FetchBookSummary)
        advanceUntilIdle()

        viewModel.tryHandleIntent(UiIntent.GoNextPart)
        verify(exactly = 0) { mockAudioPlaybackInteractor.skipForward() }
    }

    @Test
    fun `GoNextPart does nothing when player is unavailable`() = testCoroutineScope.runTest {
        coEvery { mockGetBookSummaryUseCase.execute(any()) } returns testBookSummary
        every { mockAudioPlaybackInteractor.isPlayerAvailable } returns false

        viewModel.tryHandleIntent(UiIntent.FetchBookSummary)
        advanceUntilIdle()
        viewModel.tryHandleIntent(UiIntent.GoNextPart)

        verify(exactly = 0) { mockAudioPlaybackInteractor.skipForward() }
    }

    @Test
    fun `GoPreviousPart skips to the previous audio track`() = testCoroutineScope.runTest {
        coEvery { mockGetBookSummaryUseCase.execute(any()) } returns testBookSummary
        every { mockAudioPlaybackInteractor.isPlayerAvailable } returns true

        viewModel.tryHandleIntent(UiIntent.FetchBookSummary)
        advanceUntilIdle()

        viewModel.tryHandleIntent(UiIntent.GoPreviousPart)
        verify { mockAudioPlaybackInteractor.skipBackward() }
    }

    @Test
    fun `GoPreviousPart does nothing when at the first part`() = testCoroutineScope.runTest {
        val bookSummaryOnePart = testBookSummary.copy(
            summaryParts = listOf(testSummaryParts.first())
        )
        coEvery { mockGetBookSummaryUseCase.execute(any()) } returns bookSummaryOnePart

        viewModel.tryHandleIntent(UiIntent.FetchBookSummary)
        advanceUntilIdle()

        viewModel.tryHandleIntent(UiIntent.GoPreviousPart)
        verify(exactly = 0) { mockAudioPlaybackInteractor.skipBackward() }
    }

    @Test
    fun `GoPreviousPart does nothing when player is unavailable`() = testCoroutineScope.runTest {
        coEvery { mockGetBookSummaryUseCase.execute(any()) } returns testBookSummary
        every { mockAudioPlaybackInteractor.isPlayerAvailable } returns false

        viewModel.tryHandleIntent(UiIntent.FetchBookSummary)
        advanceUntilIdle()
        viewModel.tryHandleIntent(UiIntent.GoPreviousPart)

        verify(exactly = 0) { mockAudioPlaybackInteractor.skipBackward() }
    }

    @Test
    fun `ToggleSummaryMode toggles the mode correctly`() = testCoroutineScope.runTest {
        coEvery { mockGetBookSummaryUseCase.execute(any()) } returns testBookSummary
        every { mockAudioPlaybackInteractor.isPlayerAvailable } returns true

        viewModel.tryHandleIntent(UiIntent.FetchBookSummary)
        advanceUntilIdle()

        val currentState = viewModel.uiState.value.asData ?: return@runTest
        val initialSummaryMode = currentState.isListeningModeEnabled

        viewModel.tryHandleIntent(UiIntent.ToggleSummaryMode)
        advanceTimeBy(TIME_ADVANCE_BY)

        val toggledState = viewModel.uiState.value.asData ?: return@runTest
        assertNotEquals(initialSummaryMode, toggledState.isListeningModeEnabled)
    }

    @Test
    fun `ToggleSummaryMode does nothing without valid state`() = testCoroutineScope.runTest {
        val state1 = viewModel.uiState.value
        viewModel.tryHandleIntent(UiIntent.ToggleSummaryMode)
        val state2 = viewModel.uiState.value

        assertEquals(state1, state2)
    }

    @Test
    fun `UpdatePlaybackState updates UI correctly`() = testCoroutineScope.runTest {
        coEvery { mockGetBookSummaryUseCase.execute(any()) } returns testBookSummary

        viewModel.tryHandleIntent(UiIntent.FetchBookSummary)
        advanceUntilIdle()

        val newPlaybackState = PlaybackState.Ready(
            isBuffering = false,
            isAudioPlaying = true,
            currentAudioIndex = 2,
            currentAudioPositionMs = 30000L,
            currentAudioDurationMs = 100500L,
            audioSpeedLevel = 1.0f
        )
        viewModel.tryHandleIntent(UiIntent.UpdatePlaybackState(newPlaybackState))

        val state = viewModel.uiState.value
        assert(state is UiState.Data)
        assertEquals(newPlaybackState.isAudioPlaying, state.asData?.isAudioPlaying)
        assertEquals(newPlaybackState.currentAudioIndex, state.asData?.currentPartIndex)
        assertEquals(newPlaybackState.currentAudioPositionMs, state.asData?.currentAudioPositionMs)
        assertEquals(newPlaybackState.currentAudioDurationMs, state.asData?.currentAudioDurationMs)
    }

    @Test
    fun `UpdatePlaybackState with an error state sets Error UI state`() = testCoroutineScope.runTest {
        val errorMessage = "Playback error"
        coEvery { mockGetBookSummaryUseCase.execute(any()) } returns testBookSummary
        every { mockResourceProvider.getString(any(), any()) } returns errorMessage

        viewModel.tryHandleIntent(UiIntent.FetchBookSummary)
        advanceUntilIdle()

        val errorPlaybackState = PlaybackState.Error(ERROR_PLAYER_PLAYBACK, errorMessage)
        viewModel.tryHandleIntent(UiIntent.UpdatePlaybackState(errorPlaybackState))

        val state = viewModel.uiState.value
        assert(state is UiState.Error)
        assertEquals(errorMessage, (state as UiState.Error).errorMsg)
    }

    @Test
    fun `UpdatePlaybackState with an idle state makes player not ready`() = testCoroutineScope.runTest {
        coEvery { mockGetBookSummaryUseCase.execute(any()) } returns testBookSummary

        viewModel.tryHandleIntent(UiIntent.FetchBookSummary)
        advanceUntilIdle()

        val idlePlaybackState = PlaybackState.Idle
        viewModel.tryHandleIntent(UiIntent.UpdatePlaybackState(idlePlaybackState))

        val state = viewModel.uiState.value
        assert(state is UiState.Data)
        assertFalse((state as UiState.Data).isPlayerReady)
        assertFalse(state.isAudioPlaying)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    companion object {
        private const val TIME_ADVANCE_BY = 200L
    }
}