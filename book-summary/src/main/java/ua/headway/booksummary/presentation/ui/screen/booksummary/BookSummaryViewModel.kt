package ua.headway.booksummary.presentation.ui.screen.booksummary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.session.MediaController
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
import ua.headway.booksummary.presentation.ui.resources.Constants.ErrorCodes.BookSummary.ERROR_NO_DATA_FOR_PLAYER
import ua.headway.booksummary.presentation.ui.resources.Constants.UI
import javax.inject.Inject

// TODO: Handle all the possible errors in ViewModel via ErrorHandler (single source of error handling)
//  --> Use tryHandleEvent and tryReduce ?
@HiltViewModel
class BookSummaryViewModel @Inject constructor(
    private val audioPlaybackInteractor: AudioPlaybackInteractor
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle) // TODO: Fix concurrency
    val uiState: StateFlow<UiState> = _uiState

    private lateinit var playbackState: StateFlow<PlaybackState>

    private var wasAudioPlayingBeforePositionChange: Boolean = false

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
                    _uiState.value = reduce(_uiState.value, UiResult.Loading)
                    _uiState.value = reduce(_uiState.value, fetchBookSummary())
                }
            }

            is UiIntent.InitPlayer -> { // TODO: Cover all the possible failing scenarios in the same way
                _uiState.value.asData?.let { data ->
                    initPlayer(
                        intent.mediaController,
                        data.summaryParts.map { it.audioUrl }
                    )
                    _uiState.value = reduce(_uiState.value, UiResult.Success.PlayerInitiated)
                    return
                }
                _uiState.value = reduce(
                    _uiState.value,
                    UiResult.Failure(ERROR_NO_DATA_FOR_PLAYER)
                )
            }

            is UiIntent.ToggleAudio -> {
                audioPlaybackInteractor.togglePlayback(play = intent.play)
            }

            UiIntent.StartPlaybackPositionChange -> {
                _uiState.value.asData?.let { currentState ->
                    if (currentState.isAudioPlaying) {
                        wasAudioPlayingBeforePositionChange = true
                    }
                }
                audioPlaybackInteractor.togglePlayback(play = false)
            }

            is UiIntent.FinishPlaybackPositionChange -> {
                val currentState = _uiState.value.asData ?: return
                val newAudioPositionMs = intent.newAudioPositionMs
                    .coerceIn(0, currentState.currentAudioDurationMs)
                audioPlaybackInteractor.seekTo(newAudioPositionMs)
                audioPlaybackInteractor.togglePlayback(play = wasAudioPlayingBeforePositionChange)
                wasAudioPlayingBeforePositionChange = false
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
                                currentAudioIndex = currentState.currentPartIndex,
                                currentAudioPositionMs = currentState.currentAudioPositionMs,
                                currentAudioDurationMs = currentState.currentAudioDurationMs
                            )
                        )
                    }
                    is PlaybackState.Ready -> {
                        _uiState.value = reduce(
                            _uiState.value,
                            UiResult.Success.PlaybackStateUpdated(
                                isPlayerReady = currentState.isPlayerReady,
                                isAudioPlaying = newPlaybackState.isAudioPlaying,
                                currentAudioIndex = newPlaybackState.currentAudioIndex,
                                currentAudioPositionMs = newPlaybackState.currentAudioPositionMs,
                                currentAudioDurationMs = newPlaybackState.currentAudioDurationMs
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

            UiIntent.ClearPlayer -> {
                clearPlayer()
                _uiState.value = reduce(_uiState.value, UiResult.Success.PlayerCleared)
            }
        }
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

        is UiResult.Success.PlaybackStateUpdated -> previousState.asData?.copy(
            isPlayerReady = result.isPlayerReady,
            isAudioPlaying = result.isAudioPlaying,
            currentPartIndex = result.currentAudioIndex,
            currentAudioPositionMs = result.currentAudioPositionMs,
            currentAudioDurationMs = result.currentAudioDurationMs
        ) ?: previousState

        UiResult.Success.PlayerCleared -> UiState.Idle
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
                bookCoverUrl = "https://picsum.photos/1080/1920",
            )
        )
        return uiResult
    }

    private fun initPlayer(
        mediaController: MediaController,
        audioLinks: List<String>
    ) {
        audioPlaybackInteractor.configurePlayer(
            mediaController,
            audioLinks
        )
    }

    private fun clearPlayer() {
        audioPlaybackInteractor.releasePlayer()
    }
}