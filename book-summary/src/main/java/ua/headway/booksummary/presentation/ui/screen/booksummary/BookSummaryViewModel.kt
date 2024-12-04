package ua.headway.booksummary.presentation.ui.screen.booksummary

import android.content.ComponentName
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import ua.headway.booksummary.domain.interactor.AudioPlaybackInteractor
import ua.headway.booksummary.domain.model.BookSummary
import ua.headway.booksummary.presentation.audio.AudioPlaybackService
import ua.headway.booksummary.presentation.ui.resources.Constants.ErrorCodes.BookSummary.ERROR_NO_DATA_FOR_PLAYER
import ua.headway.booksummary.presentation.ui.resources.Constants.UI
import ua.headway.booksummary.presentation.ui.resources.LocalResources
import ua.headway.booksummary.presentation.ui.resources.provider.ResourceProvider
import javax.inject.Inject

// TODO: Handle all the possible errors in ViewModel via ErrorHandler (single source of error handling)
//  --> Use tryHandleEvent and tryReduce ?
@HiltViewModel
class BookSummaryViewModel @Inject constructor(
    private val audioPlaybackInteractor: AudioPlaybackInteractor,
    private val resourceProvider: ResourceProvider
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle) // TODO: Fix concurrency
    val uiState: StateFlow<UiState> = _uiState

    private lateinit var playbackState: StateFlow<PlaybackState>

    private var isAudioPositionChangeInProgress: Boolean = false

    private val isPlayerAvailable: Boolean
        get() = audioPlaybackInteractor.isPlayerAvailable

    init {
        viewModelScope.launch {
            playbackState = audioPlaybackInteractor.subscribeToUpdates(
                viewModelScope + Dispatchers.Default
            )
            playbackState.collectLatest { newPlaybackState ->
                handleIntent(UiIntent.UpdatePlaybackState(newPlaybackState))
            }
        }
    }

    fun handleIntent(intent: UiIntent) {
        when (intent) {
            is UiIntent.FetchBookSummary -> {
                viewModelScope.launch(Dispatchers.Default) {
                    if (_uiState.value == UiState.Idle) {
                        _uiState.value = reduce(_uiState.value, UiResult.Loading)
                        _uiState.value = reduce(_uiState.value, fetchBookSummary())
                    }
                }
            }

            is UiIntent.InitPlayer -> { // TODO: Cover all the possible failing scenarios in the same way
                _uiState.value.asData?.let { data ->
                    if (!isPlayerAvailable) {
                        val sessionToken = SessionToken(intent.context, ComponentName(
                            intent.context,
                            AudioPlaybackService::class.java
                        ))
                        val mediaControllerFuture = MediaController.Builder(
                            intent.context,
                            sessionToken
                        ).buildAsync()
                        mediaControllerFuture.addListener(
                            {
                                mediaControllerFuture.get()?.let { mediaController ->
                                    setupPlayer(mediaController, getPlayerMediaItems(data))
                                    _uiState.value = reduce(_uiState.value, UiResult.Success.PlayerInitiated)
                                }
                            },
                            MoreExecutors.directExecutor()
                        )
                    }
                    return
                }
                _uiState.value = reduce(
                    _uiState.value,
                    UiResult.Failure(ERROR_NO_DATA_FOR_PLAYER)
                )
            }

            is UiIntent.ToggleAudio -> {
                val currentState = _uiState.value.asData ?: return
                audioPlaybackInteractor.togglePlayback(play = !currentState.isAudioPlaying)
            }

            UiIntent.StartPlaybackPositionChange -> {
                isAudioPositionChangeInProgress = true
            }

            is UiIntent.FinishPlaybackPositionChange -> {
                val currentState = _uiState.value.asData ?: return
                val newAudioPositionMs = intent.newAudioPositionMs
                    .coerceIn(0, currentState.currentAudioDurationMs)
                audioPlaybackInteractor.seekTo(newAudioPositionMs)
                isAudioPositionChangeInProgress = false
            }

            is UiIntent.ShiftAudioPosition -> {
                val currentState = _uiState.value.asData ?: return
                val newAudioPositionMs = (currentState.currentAudioPositionMs + intent.offsetMs)
                    .coerceIn(0, currentState.currentAudioDurationMs)
                audioPlaybackInteractor.seekTo(newAudioPositionMs)
            }

            UiIntent.ToggleAudioSpeed -> {
                val currentState = _uiState.value.asData ?: return
                val newAudioSpeedLevel = when (currentState.audioSpeedLevel) {
                    1f -> 1.5f
                    1.5f -> 2f
                    2f -> 1f
                    else -> 1f
                }
                audioPlaybackInteractor.changeSpeed(newAudioSpeedLevel)
                _uiState.value = reduce(
                    _uiState.value,
                    UiResult.Success.AudioSpeedChanged(newAudioSpeedLevel)
                )
            }

            UiIntent.GoNextPart -> {
                audioPlaybackInteractor.skipForward()
            }

            UiIntent.GoPreviousPart -> {
                audioPlaybackInteractor.skipBackward()
            }

            is UiIntent.ToggleSummaryMode -> {
                val currentState = _uiState.value.asData ?: return
                val toggledMode = !currentState.isListeningModeEnabled
                audioPlaybackInteractor.togglePlayback(play = toggledMode)
                _uiState.value = reduce(
                    _uiState.value,
                    UiResult.Success.SummaryModeToggled(toggledMode)
                )
            }

            is UiIntent.UpdatePlaybackState -> {
                val currentState = _uiState.value.asData ?: return
                when (val newPlaybackState = intent.newPlaybackState) {
                    is PlaybackState.Idle -> {
                        _uiState.value = reduce(
                            _uiState.value,
                            UiResult.Success.PlaybackStateUpdated(
                                isPlayerReady = false,
                                isAudioPlaying = false,
                                newAudioIndex = currentState.currentPartIndex,
                                newAudioPositionMs = currentState.currentAudioPositionMs,
                                newAudioDurationMs = currentState.currentAudioDurationMs
                            )
                        )
                    }
                    is PlaybackState.Ready -> {
                        _uiState.value = reduce(
                            _uiState.value,
                            UiResult.Success.PlaybackStateUpdated(
                                isPlayerReady = currentState.isPlayerReady,
                                isAudioPlaying = newPlaybackState.isAudioPlaying,
                                newAudioIndex = newPlaybackState.currentAudioIndex,
                                newAudioPositionMs = newPlaybackState.currentAudioPositionMs,
                                newAudioDurationMs = newPlaybackState.currentAudioDurationMs
                            )
                        )
                    }
                    is PlaybackState.Error -> {
                        _uiState.value = reduce(
                            _uiState.value,
                            UiResult.Failure(
                                newPlaybackState.errorCode,
                                newPlaybackState.errorMsg
                            )
                        )
                    }
                }
            }
        }
    }

    private fun getPlayerMediaItems(data: UiState.Data): List<MediaItem> =
        data.summaryParts.mapIndexed { index, summaryPart ->
            val metadata = MediaMetadata.Builder()
                .setTitle(resourceProvider.getString(
                    LocalResources.Strings.KeyPointTitle,
                    index + 1,
                    data.partsTotal
                ))
                .setDescription(summaryPart.description)
                .setArtworkUri(Uri.parse(data.bookCoverUrl))
                .build()

            MediaItem.Builder()
                .setUri(summaryPart.audioUrl)
                .setMediaMetadata(metadata)
                .build()
        }

    private fun reduce(previousState: UiState, result: UiResult): UiState = when (result) {
        UiResult.Loading -> UiState.Loading

        is UiResult.Success.BookSummaryFetched -> {
            val currentState = previousState.asData
            val currentPartIndex = currentState?.currentPartIndex ?: 0
            UiState.Data(
                summaryParts = result.bookSummary.summaryParts,
                bookCoverUrl = result.bookSummary.bookCoverUrl,
                currentPartIndex = currentPartIndex,
                currentAudioDurationMs = currentState?.currentAudioDurationMs ?: 0,
                currentAudioPositionMs = currentState?.currentAudioPositionMs ?: 0,
                audioSpeedLevel = currentState?.audioSpeedLevel ?: UI.BookSummary.AUDIO_SPEED_LEVEL_DEFAULT,
                isPlayerReady = currentState?.isPlayerReady ?: false,
                isAudioPlaying = currentState?.isAudioPlaying ?: UI.BookSummary.AUDIO_PLAYING_DEFAULT,
                isListeningModeEnabled = currentState?.isListeningModeEnabled ?: UI.BookSummary.LISTENING_ENABLED_DEFAULT
            )
        }

        is UiResult.Success.PlayerInitiated -> previousState.asData?.copy(
            isPlayerReady = true
        ) ?: previousState

        is UiResult.Success.AudioSpeedChanged -> previousState.asData?.copy(
            audioSpeedLevel = result.audioSpeedLevel
        ) ?: previousState

        is UiResult.Failure -> result.toError()

        is UiResult.Success.SummaryModeToggled -> previousState.asData?.copy(
            isListeningModeEnabled = result.isListeningModeEnabled
        ) ?: previousState

        is UiResult.Success.PlaybackStateUpdated -> previousState.asData?.let { data ->
            val newAudioPositionMs = result.newAudioPositionMs.takeIf {
                !isAudioPositionChangeInProgress
            } ?: data.currentAudioPositionMs

            data.copy(
                isPlayerReady = result.isPlayerReady,
                isAudioPlaying = result.isAudioPlaying,
                currentPartIndex = result.newAudioIndex,
                currentAudioPositionMs = newAudioPositionMs.positive,
                currentAudioDurationMs = result.newAudioDurationMs.positive
            )
        } ?: previousState
    }

    private suspend fun fetchBookSummary(): UiResult {
        // TODO: Fetch real book data
        delay(1000)
        val uiResult = UiResult.Success.BookSummaryFetched(
            BookSummary(
                listOf(
                    BookSummary.SummaryPart(
                        description = "Summary part 1",
                        audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/BabyElephantWalk60.wav",
                        text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum",
                    ),
                    BookSummary.SummaryPart(
                        description = "Summary part 2",
                        audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/StarWars60.wav",
                        text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum",
                    ),
                    BookSummary.SummaryPart(
                        description = "Summary part 3",
                        audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/CantinaBand60.wav",
                        text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum",
                    ),
                    BookSummary.SummaryPart(
                        description = "Summary part 4",
                        audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/ImperialMarch60.wav",
                        text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum",
                    ),
                    BookSummary.SummaryPart(
                        description = "Summary part 5",
                        audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/PinkPanther60.wav",
                        text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum",
                    ),
                ),
                bookCoverUrl = "https://picsum.photos/id/24/1080/1920",
            )
        )
        return uiResult
    }

    private fun setupPlayer(
        mediaController: MediaController,
        audioItems: List<MediaItem>
    ) {
        audioPlaybackInteractor.configurePlayer(
            mediaController,
            audioItems
        )
    }

    private fun clearPlayer() {
        audioPlaybackInteractor.releasePlayer()
    }

    override fun onCleared() {
        clearPlayer()
        super.onCleared()
    }

    private val Long.positive: Long
        get() = this.coerceAtLeast (0)
}