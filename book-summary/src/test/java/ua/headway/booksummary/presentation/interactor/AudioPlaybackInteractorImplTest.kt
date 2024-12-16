package ua.headway.booksummary.presentation.interactor

import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import io.mockk.Called
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ua.headway.booksummary.presentation.ui.screen.booksummary.PlaybackState

@OptIn(ExperimentalCoroutinesApi::class)
class AudioPlaybackInteractorTest {
    private lateinit var interactor: AudioPlaybackInteractorImpl

    private val mockMediaController = mockk<MediaController>(relaxed = true)
    private val mockMediaItem = mockk<MediaItem>(relaxed = true)
    private val testCoroutineScope = TestScope()
    private val dispatcher = StandardTestDispatcher(testCoroutineScope.testScheduler)

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        interactor = AudioPlaybackInteractorImpl()
    }

    @Test
    fun `configurePlayer should initialize media player with given items`() = testCoroutineScope.runTest {
        val audioItems = listOf(mockMediaItem, mockMediaItem, mockMediaItem)
        every { mockMediaController.addListener(any()) } just runs
        every { mockMediaController.setMediaItems(audioItems) } just runs
        every { mockMediaController.prepare() } just runs

        interactor.configurePlayer(mockMediaController, audioItems)

        verify { mockMediaController.addListener(any()) }
        verify { mockMediaController.setMediaItems(audioItems) }
        verify { mockMediaController.prepare() }
        assert(interactor.isPlayerAvailable)
    }

    @Test
    fun `configurePlayer should handle empty media items gracefully`() {
        val audioItems = emptyList<MediaItem>()

        interactor.configurePlayer(mockMediaController, audioItems)

        verify { mockMediaController.setMediaItems(audioItems) }
        verify { mockMediaController.prepare() }
        assert(interactor.isPlayerAvailable)
    }

    @Test
    fun `configurePlayer called multiple times should reset previous player state`() {
        val audioItems = listOf(mockMediaItem)

        interactor.configurePlayer(mockMediaController, audioItems)
        interactor.configurePlayer(mockMediaController, audioItems)

        verify(exactly = 2) { mockMediaController.setMediaItems(audioItems) }
        verify(exactly = 2) { mockMediaController.prepare() }
    }

    @Test
    fun `togglePlayback should play when play is false`() {
        every { mockMediaController.isPlaying } returns false

        interactor.configurePlayer(mockMediaController, listOf(mockMediaItem))
        interactor.togglePlayback(true)

        verify { mockMediaController.play() }
    }

    @Test
    fun `togglePlayback should pause when play is true`() {
        every { mockMediaController.isPlaying } returns true

        interactor.configurePlayer(mockMediaController, listOf(mockMediaItem))
        interactor.togglePlayback(false)

        verify { mockMediaController.pause() }
    }

    @Test
    fun `togglePlayback should not call play when already playing`() {
        every { mockMediaController.isPlaying } returns true

        interactor.configurePlayer(mockMediaController, listOf(mockMediaItem))
        interactor.togglePlayback(true)

        verify(exactly = 0) { mockMediaController.play() }
    }

    @Test
    fun `togglePlayback should not call pause when already paused`() {
        every { mockMediaController.isPlaying } returns false

        interactor.configurePlayer(mockMediaController, listOf(mockMediaItem))
        interactor.togglePlayback(false)

        verify(exactly = 0) { mockMediaController.pause() }
    }

    @Test
    fun `togglePlayback does nothing when audioPlayer is null`() {
        interactor.togglePlayback(true)

        verify { mockMediaController wasNot Called }
    }

    @Test
    fun `seekTo should seek to the correct position`() = testCoroutineScope.runTest {
        val positionMs = 5000L
        every { mockMediaController.duration } returns 10000L
        every { mockMediaController.seekTo(positionMs) } just runs

        interactor.configurePlayer(mockMediaController, listOf(mockMediaItem))
        interactor.seekTo(positionMs)

        verify { mockMediaController.seekTo(positionMs) }
    }

    @Test
    fun `seekTo clamps position to zero when value is negative`() {
        every { mockMediaController.seekTo(0) } just runs

        interactor.configurePlayer(mockMediaController, listOf(mockMediaItem))
        interactor.seekTo(-1000L)

        verify { mockMediaController.seekTo(0) }
    }

    @Test
    fun `seekTo clamps position to duration when value exceeds track duration`() {
        val maxDuration = 5000L
        every { mockMediaController.duration } returns maxDuration
        every { mockMediaController.seekTo(maxDuration) } just runs

        interactor.configurePlayer(mockMediaController, listOf(mockMediaItem))
        interactor.seekTo(10000L)

        verify { mockMediaController.seekTo(maxDuration) }
    }

    @Test
    fun `seekTo should do nothing when player is null`() {
        interactor.seekTo(1000L)

        verify { mockMediaController wasNot Called }
    }

    @Test
    fun `seekTo should handle null position safely`() {
        interactor.configurePlayer(mockMediaController, listOf(mockMediaItem))

        interactor.seekTo(positionMs = 0L)

        verify { mockMediaController.seekTo(0L) }
    }

    @Test
    fun `changeSpeed should update playback speed`() {
        val speed = 1.5f
        every { mockMediaController.setPlaybackSpeed(speed) } just runs

        interactor.configurePlayer(mockMediaController, listOf(mockMediaItem))
        interactor.changeSpeed(speed)

        verify { mockMediaController.setPlaybackSpeed(speed) }
    }

    @Test
    fun `changeSpeed should handle invalid speed values`() {
        interactor.configurePlayer(mockMediaController, listOf(mockMediaItem))

        interactor.changeSpeed(0.0f)
        interactor.changeSpeed(Float.NaN)

        verify(exactly = 0) { mockMediaController.setPlaybackSpeed(any()) }
    }

    @Test
    fun `skipForward should seek to next media item`() {
        every { mockMediaController.seekToNext() } just runs

        interactor.configurePlayer(mockMediaController, listOf(mockMediaItem))
        interactor.skipForward()

        verify { mockMediaController.seekToNext() }
    }

    @Test
    fun `skipBackward should seek to previous media item`() {
        every { mockMediaController.seekToPrevious() } just runs

        interactor.configurePlayer(mockMediaController, listOf(mockMediaItem))
        interactor.skipBackward()

        verify { mockMediaController.seekToPrevious() }
    }

    @Test
    fun `skipForward should do nothing when player is null`() {
        interactor.skipForward()

        verify { mockMediaController wasNot Called }
    }

    @Test
    fun `skipBackward should do nothing when player is null`() {
        interactor.skipBackward()

        verify { mockMediaController wasNot Called }
    }

    @Test
    fun `releasePlayer should clean up media player resources`() {
        every { mockMediaController.removeListener(any()) } just runs
        every { mockMediaController.stop() } just runs
        every { mockMediaController.release() } just runs

        interactor.configurePlayer(mockMediaController, listOf(mockMediaItem))
        interactor.releasePlayer()

        verify { mockMediaController.removeListener(any()) }
        verify { mockMediaController.stop() }
        verify { mockMediaController.release() }
        assert(!interactor.isPlayerAvailable)
    }

    @Test
    fun `subscribeToUpdates returns valid playback StateFlow with up-to-date states`() = testCoroutineScope.runTest {
        val listenerSlot = slot<Player.Listener>()
        every { mockMediaController.addListener(capture(listenerSlot)) } just runs

        interactor.configurePlayer(mockMediaController, listOf(mockMediaItem))
        val testStateFlow = interactor.subscribeToUpdates(this.backgroundScope)

        listenerSlot.captured.onPlaybackStateChanged(Player.STATE_READY)
        advanceTimeBy(TIME_ADVANCE_BY)

        listenerSlot.captured.onPlaybackStateChanged(Player.STATE_IDLE)
        advanceTimeBy(TIME_ADVANCE_BY)

        val state = testStateFlow.value
        assert(state is PlaybackState.Idle)
    }

    @Test
    fun `onPlaybackStateChanged updates to READY state`() = testCoroutineScope.runTest {
        val listenerSlot = slot<Player.Listener>()
        every { mockMediaController.addListener(capture(listenerSlot)) } just runs

        interactor.configurePlayer(mockMediaController, listOf(mockMediaItem))
        val testStateFlow = interactor.subscribeToUpdates(this.backgroundScope)

        listenerSlot.captured.onPlaybackStateChanged(Player.STATE_READY)
        advanceTimeBy(TIME_ADVANCE_BY)

        val state = testStateFlow.value
        assert(state is PlaybackState.Ready)
        assertEquals(false, (state as PlaybackState.Ready).isBuffering)
    }

    @Test
    fun `onPlaybackStateChanged handles BUFFERING state`() = testCoroutineScope.runTest {
        val listenerSlot = slot<Player.Listener>()
        every { mockMediaController.addListener(capture(listenerSlot)) } just runs

        interactor.configurePlayer(mockMediaController, listOf(mockMediaItem))
        val testStateFlow = interactor.subscribeToUpdates(this.backgroundScope)

        listenerSlot.captured.onPlaybackStateChanged(Player.STATE_BUFFERING)
        advanceTimeBy(TIME_ADVANCE_BY)

        val state = testStateFlow.value
        assert(state is PlaybackState.Idle)
    }

    @Test
    fun `onIsPlayingChanged updates playback state correctly`() = testCoroutineScope.runTest {
        val listenerSlot = slot<Player.Listener>()
        every { mockMediaController.addListener(capture(listenerSlot)) } just runs
        every { mockMediaController.isPlaying } returns true

        interactor.configurePlayer(mockMediaController, listOf(mockMediaItem))
        val testStateFlow = interactor.subscribeToUpdates(this.backgroundScope)

        listenerSlot.captured.onPlaybackStateChanged(Player.STATE_READY)
        advanceTimeBy(TIME_ADVANCE_BY)

        listenerSlot.captured.onIsPlayingChanged(true)
        advanceTimeBy(TIME_ADVANCE_BY)

        val state = testStateFlow.value
        assert(state is PlaybackState.Ready)
        assertEquals(true, state.asReady?.isAudioPlaying)
    }

    @Test
    fun `onMediaItemTransition updates current audio index and duration`() = testCoroutineScope.runTest {
        val listenerSlot = slot<Player.Listener>()
        every { mockMediaController.addListener(capture(listenerSlot)) } just runs
        every { mockMediaController.duration } returns 5000L
        every { mockMediaController.currentMediaItemIndex } returns 2

        interactor.configurePlayer(mockMediaController, listOf(mockMediaItem, mockMediaItem, mockMediaItem))
        val testStateFlow = interactor.subscribeToUpdates(this.backgroundScope)

        listenerSlot.captured.onPlaybackStateChanged(Player.STATE_READY)
        advanceTimeBy(TIME_ADVANCE_BY)

        listenerSlot.captured.onMediaItemTransition(
            mockMediaItem,
            Player.MEDIA_ITEM_TRANSITION_REASON_SEEK
        )
        advanceTimeBy(TIME_ADVANCE_BY)

        val state = testStateFlow.value
        assert(state is PlaybackState.Ready)
        assertEquals(2, (state as PlaybackState.Ready).currentAudioIndex)
        assertEquals(5000L, state.currentAudioDurationMs)
    }

    @Test
    fun `onMediaItemTransition does nothing when mediaItem is null`() = testCoroutineScope.runTest {
        val listenerSlot = slot<Player.Listener>()
        every { mockMediaController.addListener(capture(listenerSlot)) } just runs
        every { mockMediaController.currentMediaItemIndex } returns 1

        interactor.configurePlayer(mockMediaController, listOf(mockMediaItem))
        val testStateFlow = interactor.subscribeToUpdates(this.backgroundScope)

        listenerSlot.captured.onPlaybackStateChanged(Player.STATE_READY)
        advanceTimeBy(TIME_ADVANCE_BY)
        val state1 = testStateFlow.value

        every { mockMediaController.currentMediaItemIndex } returns 2
        listenerSlot.captured.onMediaItemTransition(
            null,
            Player.MEDIA_ITEM_TRANSITION_REASON_SEEK
        )
        advanceTimeBy(TIME_ADVANCE_BY)
        val state2 = testStateFlow.value

        assertEquals(state1, state2)
    }

    @Test
    fun `onPlayerError sets error state`() = testCoroutineScope.runTest {
        val listenerSlot = slot<Player.Listener>()
        every { mockMediaController.addListener(capture(listenerSlot)) } just runs

        val errorMessage = "Playback Error"
        val mockPlaybackException = mockk<PlaybackException>()
        every { mockPlaybackException.message } returns errorMessage

        interactor.configurePlayer(mockMediaController, listOf(mockMediaItem))
        val testStateFlow = interactor.subscribeToUpdates(this.backgroundScope)

        listenerSlot.captured.onPlayerError(mockPlaybackException)
        advanceTimeBy(TIME_ADVANCE_BY)

        val state = testStateFlow.value
        assert(state is PlaybackState.Error)
        assertEquals(errorMessage, (state as PlaybackState.Error).errorMsg)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    companion object {
        private const val TIME_ADVANCE_BY = 200L
    }
}