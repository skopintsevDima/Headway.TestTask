package ua.headway.booksummary.presentation.ui.screen.booksummary

import android.content.Context
import ua.headway.booksummary.domain.model.BookSummary
import ua.headway.booksummary.domain.model.BookSummary.SummaryPart
import ua.headway.booksummary.presentation.ui.resources.Constants.ErrorCodes

sealed class UiState {
    data object Idle: UiState()
    data object Loading: UiState()

    data class Data(
        val summaryParts: List<SummaryPart>,
        val bookCoverUrl: String,
        val currentPartIndex: Int,
        val isPlayerReady: Boolean, // TODO: Adapt UI to react on player availability (one more UiState? Info?)
        val isAudioPlaying: Boolean,
        val currentAudioPositionMs: Long,
        val currentAudioDurationMs: Long,
        val audioSpeedLevel: Float,
        val isListeningModeEnabled: Boolean
    ): UiState() {
        val currentSummaryPart: SummaryPart
            get() = summaryParts[currentPartIndex]
        val partsTotal: Int
            get() = summaryParts.size
    }

    sealed class Error(val errorMsg: String): UiState() {
        // TODO: Move SummaryPartsAreOver to Info UiState - not Error!
        data object NoDataForPlayer: Error("Goddamn! We have nothing to play for you 😔")
        data object UnknownError: Error("Something unknown happened...but Trump will fix it...")
    }

    val asData: Data?
        get() = this as? Data
}

sealed class UiIntent {
    data object FetchBookSummary: UiIntent()
    data class InitPlayer(val context: Context) : UiIntent()
    data object ToggleAudio: UiIntent()
    data object StartPlaybackPositionChange: UiIntent()
    data class FinishPlaybackPositionChange(val newAudioPositionMs: Long): UiIntent()
    data class ShiftAudioPosition(val offsetMs: Long): UiIntent()
    data object ToggleAudioSpeed: UiIntent()
    data object GoNextPart: UiIntent()
    data object GoPreviousPart: UiIntent()
    data object ToggleSummaryMode: UiIntent()
    data class UpdatePlaybackState(val newPlaybackState: PlaybackState): UiIntent()
}

sealed class UiResult {
    data object Loading: UiResult()

    sealed class Success: UiResult() {
        data class BookSummaryFetched(val bookSummary: BookSummary): Success()
        data object PlayerInitiated: Success()
        data class AudioSpeedChanged(val audioSpeedLevel: Float): Success()
        data class SummaryModeToggled(val isListeningModeEnabled: Boolean): Success()
        data class PlaybackStateUpdated(
            val isPlayerReady: Boolean,
            val isAudioPlaying: Boolean,
            val newAudioIndex: Int,
            val newAudioPositionMs: Long,
            val newAudioDurationMs: Long
        ): Success()
    }

    data class Failure(val errorCode: Int, val errorMsg: String = ""): UiResult() {
        fun toError(): UiState.Error = when (errorCode) {
            ErrorCodes.BookSummary.ERROR_NO_DATA_FOR_PLAYER -> UiState.Error.NoDataForPlayer
            else -> UiState.Error.UnknownError
        }
    }
}

sealed class PlaybackState {
    data object Idle: PlaybackState()

    data class Ready(
        val isAudioPlaying: Boolean,
        val currentAudioIndex: Int,
        val currentAudioPositionMs: Long,
        val currentAudioDurationMs: Long
    ): PlaybackState()

    data class Error(
        val errorCode: Int,
        val errorMsg: String = ""
    ): PlaybackState()

    val asReady: Ready?
        get() = this as? Ready
}