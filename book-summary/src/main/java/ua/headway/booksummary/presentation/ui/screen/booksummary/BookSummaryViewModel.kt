package ua.headway.booksummary.presentation.ui.screen.booksummary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ua.headway.booksummary.presentation.ui.resources.Constants.ErrorCodes
import ua.headway.booksummary.presentation.ui.resources.Constants.UI
import ua.headway.booksummary.domain.model.BookSummary
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class BookSummaryViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    fun handleIntent(intent: UiIntent) {
        viewModelScope.launch {
            when (intent) {
                is UiIntent.FetchBookSummary -> {
                    _uiState.value = reduce(_uiState.value, UiResult.Loading)
                    _uiState.value = reduce(_uiState.value, fetchBookSummary())
                }

                is UiIntent.ToggleAudio -> {
                    val currentState = _uiState.value.asData ?: return@launch
                    // TODO: Toggle audio player
                    val toggledMode = !currentState.isAudioPlaying
                    _uiState.value = reduce(
                        _uiState.value,
                        UiResult.Success.AudioToggled(toggledMode)
                    )
                }

                is UiIntent.ChangeAudioPosition -> {
                    val currentState = _uiState.value.asData ?: return@launch
                    // TODO: Move audio player
                    val newPosition = (currentState.currentAudioPositionMs + intent.offsetMs)
                        .coerceIn(0f, currentState.currentSummaryPart.audioDurationMs)
                    _uiState.value = reduce(
                        _uiState.value,
                        UiResult.Success.AudioPositionChanged(newPosition)
                    )
                }

                is UiIntent.SetAudioPosition -> {
                    _uiState.value.asData ?: return@launch
                    // TODO: Move audio player
                    _uiState.value = reduce(
                        _uiState.value,
                        UiResult.Success.AudioPositionSet(intent.newAudioPositionMs)
                    )
                }

                UiIntent.ToggleAudioSpeed -> {
                    val currentState = _uiState.value.asData ?: return@launch
                    // TODO: Change audio player speed
                    val newAudioSpeedLevel = when (currentState.audioSpeedLevel) {
                        1f -> 1.5f
                        1.5f -> 2f
                        2f -> 1f
                        else -> 1f
                    }
                    _uiState.value = reduce(
                        _uiState.value,
                        UiResult.Success.AudioSpeedChanged(newAudioSpeedLevel)
                    )
                }

                UiIntent.GoNextPart -> {
                    val currentState = _uiState.value.asData ?: return@launch
                    val newPartIndex = currentState.currentPartIndex + 1
                    if (newPartIndex <= currentState.partsTotal - 1) {
                        // TODO: Let audio player know
                        _uiState.value = reduce(_uiState.value, UiResult.Success.PartSkipped(newPartIndex))
                    } else {
                        _uiState.value = reduce(
                            _uiState.value,
                            UiResult.Failure(ErrorCodes.BookSummary.ERROR_SUMMARY_PARTS_ARE_OVER)
                        )
                    }
                }

                UiIntent.GoPreviousPart -> {
                    val currentState = _uiState.value.asData ?: return@launch
                    val newPartIndex = max(currentState.currentPartIndex - 1, 0)
                    // TODO: Let audio player know
                    _uiState.value = reduce(_uiState.value, UiResult.Success.PartRewound(newPartIndex))
                }

                is UiIntent.ToggleSummaryMode -> {
                    val currentState = _uiState.value.asData ?: return@launch
                    // TODO: Let audio player know
                    val toggledMode = !currentState.isListeningModeEnabled
                    _uiState.value = reduce(
                        _uiState.value,
                        UiResult.Success.SummaryModeToggled(toggledMode)
                    )
                }
            }
        }
    }

    private suspend fun fetchBookSummary(): UiResult {
        // TODO: Fetch real book data
        delay(1000)
        return UiResult.Success.BookSummaryFetched(
            BookSummary(
                listOf(
                    BookSummary.SummaryPart(
                        description = "Summary part 1",
                        audioUrl = "",
                        audioDurationMs = 154f * 1000,
                        text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum",
                    ),
                    BookSummary.SummaryPart(
                        description = "Summary part 2",
                        audioUrl = "",
                        audioDurationMs = 123f * 1000,
                        text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum",
                    ),
                    BookSummary.SummaryPart(
                        description = "Summary part 3",
                        audioUrl = "",
                        audioDurationMs = 61f * 1000,
                        text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum",
                    ),
                    BookSummary.SummaryPart(
                        description = "Summary part 4",
                        audioUrl = "",
                        audioDurationMs = 35f * 1000,
                        text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum",
                    ),
                    BookSummary.SummaryPart(
                        description = "Summary part 5",
                        audioUrl = "",
                        audioDurationMs = 347f * 1000,
                        text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum",
                    ),
                ),
                bookCoverUrl = "https://picsum.photos/1080/1920",
            )
        )
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
                currentAudioPositionMs = currentState?.currentAudioPositionMs ?: 0f,
                audioSpeedLevel = currentState?.audioSpeedLevel ?: UI.BookSummary.AUDIO_SPEED_LEVEL_DEFAULT,
                isAudioPlaying = currentState?.isAudioPlaying ?: UI.BookSummary.AUDIO_PLAYING_DEFAULT,
                isListeningModeEnabled = currentState?.isListeningModeEnabled ?: UI.BookSummary.LISTENING_ENABLED_DEFAULT
            )
        }

        is UiResult.Success.AudioToggled -> previousState.asData?.copy(
            isAudioPlaying = result.isAudioPlaying
        ) ?: previousState

        is UiResult.Success.AudioPositionChanged -> previousState.asData?.copy(
            currentAudioPositionMs = result.currentAudioPosition
        ) ?: previousState

        is UiResult.Success.AudioPositionSet -> previousState.asData?.copy(
            currentAudioPositionMs = result.currentAudioPosition
        ) ?: previousState

        is UiResult.Success.AudioSpeedChanged -> previousState.asData?.copy(
            audioSpeedLevel = result.audioSpeedLevel
        ) ?: previousState

        is UiResult.Success.PartSkipped -> previousState.asData?.copy(
            currentPartIndex = result.currentPartIndex,
            currentAudioPositionMs = 0f
        ) ?: previousState

        is UiResult.Success.PartRewound -> previousState.asData?.copy(
            currentPartIndex = result.currentPartIndex,
            currentAudioPositionMs = 0f
        ) ?: previousState

        is UiResult.Failure -> result.toError()

        is UiResult.Success.SummaryModeToggled -> previousState.asData?.copy(
            isListeningModeEnabled = result.isListeningModeEnabled
        ) ?: previousState
    }
}