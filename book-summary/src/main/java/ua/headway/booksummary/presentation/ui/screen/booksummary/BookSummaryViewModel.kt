package ua.headway.booksummary.presentation.ui.screen.booksummary

import android.content.ComponentName
import android.content.Context
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
import ua.headway.booksummary.presentation.ui.resources.Constants.ErrorCodes.BookSummary.ERROR_LOAD_BOOK_DATA
import ua.headway.booksummary.presentation.ui.resources.Constants.ErrorCodes.BookSummary.ERROR_NO_DATA_FOR_PLAYER
import ua.headway.booksummary.presentation.ui.resources.Constants.ErrorCodes.BookSummary.ERROR_PLAYER_INIT
import ua.headway.booksummary.presentation.ui.resources.Constants.ErrorCodes.BookSummary.ERROR_PLAYER_SEEK_FAILED
import ua.headway.booksummary.presentation.ui.resources.Constants.ErrorCodes.BookSummary.ERROR_PLAYER_SKIP_FAILED
import ua.headway.booksummary.presentation.ui.resources.Constants.ErrorCodes.BookSummary.ERROR_PLAYER_SPEED_CHANGE_FAILED
import ua.headway.booksummary.presentation.ui.resources.Constants.ErrorCodes.BookSummary.ERROR_PLAYER_TOGGLE_FAILED
import ua.headway.booksummary.presentation.ui.resources.Constants.ErrorCodes.BookSummary.ERROR_UNKNOWN
import ua.headway.booksummary.presentation.ui.resources.Constants.UI
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.BookSummary.ERROR_MSG_PLAYER_SEEK_FAILED
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.BookSummary.ERROR_MSG_PLAYER_SKIP_FAILED
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.BookSummary.ERROR_MSG_PLAYER_SPEED_CHANGE_FAILED
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.BookSummary.ERROR_MSG_PLAYER_TOGGLE_FAILED
import ua.headway.booksummary.presentation.ui.resources.LocalResources
import ua.headway.booksummary.presentation.ui.resources.provider.ResourceProvider
import ua.headway.booksummary.presentation.ui.screen.booksummary.UiState.Data.NonCriticalError
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
                tryHandleIntent(UiIntent.UpdatePlaybackState(newPlaybackState))
            }
        }
    }

    fun tryHandleIntent(intent: UiIntent) {
        try {
            handleIntent(intent)
        } catch (e: Throwable) {
            _uiState.value = reduce(
                _uiState.value,
                UiResult.Failure(ERROR_UNKNOWN, e.message.toString())
            )
        }
    }

    private fun handleIntent(intent: UiIntent) {
        when (intent) {
            is UiIntent.FetchBookSummary -> {
                viewModelScope.launch(Dispatchers.Default) {
                    if (_uiState.value !is UiState.Data) {
                        _uiState.value = reduce(_uiState.value, UiResult.Loading)
                        _uiState.value = reduce(_uiState.value, fetchBookSummary())
                    }
                }
            }

            is UiIntent.InitPlayer -> {
                _uiState.value.asData?.let { data ->
                    setupPlayer(
                        context = intent.context,
                        audioItems = getPlayerAudioItems(data),
                        onSuccess = {
                            _uiState.value = reduce(_uiState.value, UiResult.Success.PlayerInitiated)
                        },
                        onFailure = { error ->
                            _uiState.value = reduce(
                                _uiState.value,
                                UiResult.Failure(ERROR_PLAYER_INIT, error.message.toString())
                            )
                        }
                    )
                    return
                }
                _uiState.value = reduce(
                    _uiState.value,
                    UiResult.Failure(ERROR_NO_DATA_FOR_PLAYER)
                )
            }

            is UiIntent.ToggleAudio -> {
                val currentState = _uiState.value.asData ?: return
                audioPlaybackInteractor.interactSafe(ERROR_PLAYER_TOGGLE_FAILED) {
                    togglePlayback(play = !currentState.isAudioPlaying)
                }
            }

            UiIntent.StartPlaybackPositionChange -> {
                isAudioPositionChangeInProgress = true
            }

            is UiIntent.FinishPlaybackPositionChange -> {
                val currentState = _uiState.value.asData ?: return
                val newAudioPositionMs = intent.newAudioPositionMs
                    .coerceIn(0, currentState.currentAudioDurationMs)
                audioPlaybackInteractor.interactSafe(ERROR_PLAYER_SEEK_FAILED) {
                    seekTo(newAudioPositionMs)
                    isAudioPositionChangeInProgress = false
                }
            }

            is UiIntent.ShiftAudioPosition -> {
                val currentState = _uiState.value.asData ?: return
                val newAudioPositionMs = (currentState.currentAudioPositionMs + intent.offsetMs)
                    .coerceIn(0, currentState.currentAudioDurationMs)
                audioPlaybackInteractor.interactSafe(ERROR_PLAYER_SEEK_FAILED) {
                    seekTo(newAudioPositionMs)
                }
            }

            UiIntent.ToggleAudioSpeed -> {
                val currentState = _uiState.value.asData ?: return
                val newAudioSpeedLevel = when (currentState.audioSpeedLevel) {
                    1f -> 1.5f
                    1.5f -> 2f
                    2f -> 1f
                    else -> 1f
                }
                audioPlaybackInteractor.interactSafe(ERROR_PLAYER_SPEED_CHANGE_FAILED) {
                    changeSpeed(newAudioSpeedLevel)
                    _uiState.value = reduce(
                        _uiState.value,
                        UiResult.Success.AudioSpeedChanged(newAudioSpeedLevel)
                    )
                }
            }

            UiIntent.GoNextPart -> {
                audioPlaybackInteractor.interactSafe(ERROR_PLAYER_SKIP_FAILED) {
                    skipForward()
                }
            }

            UiIntent.GoPreviousPart -> {
                audioPlaybackInteractor.interactSafe(ERROR_PLAYER_SKIP_FAILED) {
                    skipBackward()
                }
            }

            is UiIntent.ToggleSummaryMode -> {
                val currentState = _uiState.value.asData ?: return
                val toggledMode = !currentState.isListeningModeEnabled
                audioPlaybackInteractor.interactSafe(ERROR_PLAYER_TOGGLE_FAILED) {
                    togglePlayback(play = toggledMode)
                    _uiState.value = reduce(
                        _uiState.value,
                        UiResult.Success.SummaryModeToggled(toggledMode)
                    )
                }
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

    private fun getPlayerAudioItems(data: UiState.Data): List<MediaItem> =
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

        is UiResult.Failure -> {
            val onNonCriticalErrorOccurred = { errorMsg: String ->
                previousState.asData?.let { data ->
                    val newNonCriticalError = data.nonCriticalError?.copy(
                        id = data.nonCriticalError.id + 1,
                        errorMsg = errorMsg
                    ) ?: NonCriticalError(0, errorMsg)
                    data.copy(nonCriticalError = newNonCriticalError)
                } ?: previousState
            }

            when (result.errorCode) {
                ERROR_PLAYER_SEEK_FAILED -> onNonCriticalErrorOccurred(ERROR_MSG_PLAYER_SEEK_FAILED)
                ERROR_PLAYER_TOGGLE_FAILED -> onNonCriticalErrorOccurred(ERROR_MSG_PLAYER_TOGGLE_FAILED)
                ERROR_PLAYER_SPEED_CHANGE_FAILED -> onNonCriticalErrorOccurred(ERROR_MSG_PLAYER_SPEED_CHANGE_FAILED)
                ERROR_PLAYER_SKIP_FAILED -> onNonCriticalErrorOccurred(ERROR_MSG_PLAYER_SKIP_FAILED)
                else -> result.toError()
            }
        }

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
        try {
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
        } catch (e: Throwable) {
            return UiResult.Failure(ERROR_LOAD_BOOK_DATA, e.message.toString())
        }
    }

    private fun setupPlayer(
        context: Context,
        audioItems: List<MediaItem>,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        try {
            if (!isPlayerAvailable) {
                val sessionToken = SessionToken(context, ComponentName(context, AudioPlaybackService::class.java))
                val mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
                mediaControllerFuture.addListener(
                    {
                        try {
                            mediaControllerFuture.get()?.let { mediaController ->
                                audioPlaybackInteractor.configurePlayer(mediaController, audioItems)
                                onSuccess.invoke()
                            }
                        } catch (e: Throwable) {
                            onFailure.invoke(e)
                        }
                    },
                    MoreExecutors.directExecutor()
                )
            }
        } catch (e: Throwable) {
            onFailure.invoke(e)
        }
    }

    private fun clearPlayer() {
        audioPlaybackInteractor.interactSafe(ERROR_UNKNOWN) {
            releasePlayer()
        }
    }

    override fun onCleared() {
        clearPlayer()
        super.onCleared()
    }

    private val Long.positive: Long
        get() = this.coerceAtLeast (0)

    private fun AudioPlaybackInteractor.interactSafe(
        onErrorCode: Int,
        interaction: AudioPlaybackInteractor.() -> Unit
    ) {
        try {
            this.interaction()
        } catch (e: Throwable) {
            _uiState.value = reduce(
                _uiState.value,
                UiResult.Failure(onErrorCode)
            )
        }
    }
}