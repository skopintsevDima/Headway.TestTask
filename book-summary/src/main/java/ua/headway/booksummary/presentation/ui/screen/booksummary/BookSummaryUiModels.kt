package ua.headway.booksummary.presentation.ui.screen.booksummary

import android.content.Context
import androidx.compose.runtime.Stable
import ua.headway.booksummary.domain.model.BookSummaryModel
import ua.headway.booksummary.domain.model.BookSummaryModel.SummaryPart
import ua.headway.booksummary.presentation.ui.resources.Constants.ErrorCodes
import ua.headway.booksummary.presentation.ui.resources.LocalResources
import ua.headway.booksummary.presentation.ui.resources.provider.ResourceProvider

sealed class UiState {
    data object Idle: UiState()
    data object Loading: UiState()

    data class Data(
        @Stable val summaryParts: List<SummaryPart>,
        val bookCoverUrl: String,
        val currentPartIndex: Int,
        val isPlayerReady: Boolean,
        val isAudioPlaying: Boolean,
        val currentAudioPositionMs: Long,
        val currentAudioDurationMs: Long,
        val audioSpeedLevel: Float,
        val isListeningModeEnabled: Boolean,
        val nonCriticalError: NonCriticalError? = null
    ): UiState() {
        val currentSummaryPart: SummaryPart
            get() = summaryParts[currentPartIndex]
        val partsTotal: Int
            get() = summaryParts.size
        val isFirstPartNow: Boolean
            get() = currentPartIndex == 0
        val isLastPartNow: Boolean
            get() = currentPartIndex == partsTotal - 1

        data class NonCriticalError(
            val id: Long,
            val errorMsg: String
        )
    }

    sealed class Error(val errorMsg: String): UiState() {
        data class NoDataForPlayerError(val msg: String): Error(msg)
        data class LoadBookDataError(val msg: String): Error(msg)
        data class PlayerInitError(val msg: String): Error(msg)
        data class PlaybackError(val msg: String): Error(msg)
        data class UnknownError(val msg: String): Error(msg)
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
        data class BookSummaryFetched(val bookSummary: BookSummaryModel): Success()
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
        fun toError(resourceProvider: ResourceProvider): UiState.Error = when (errorCode) {
            ErrorCodes.BookSummary.ERROR_NO_DATA_FOR_PLAYER -> UiState.Error.NoDataForPlayerError(
                resourceProvider.getString(LocalResources.Strings.ErrorNoDataForPlayer)
            )
            ErrorCodes.BookSummary.ERROR_LOAD_BOOK_DATA -> UiState.Error.LoadBookDataError(
                resourceProvider.getString(LocalResources.Strings.ErrorLoadBookData, errorMsg)
            )
            ErrorCodes.BookSummary.ERROR_PLAYER_INIT -> UiState.Error.PlayerInitError(
                resourceProvider.getString(LocalResources.Strings.ErrorPlayerInit, errorMsg)
            )
            ErrorCodes.BookSummary.ERROR_PLAYER_PLAYBACK -> UiState.Error.PlaybackError(
                resourceProvider.getString(LocalResources.Strings.ErrorPlayback, errorMsg)
            )
            else -> UiState.Error.UnknownError(
                resourceProvider.getString(LocalResources.Strings.ErrorUnknown)
            )
        }
    }
}

sealed class PlaybackState {
    data object Idle: PlaybackState()

    data class Ready(
        val isBuffering: Boolean,
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