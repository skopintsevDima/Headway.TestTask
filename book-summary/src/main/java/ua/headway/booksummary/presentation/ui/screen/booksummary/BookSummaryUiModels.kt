package ua.headway.booksummary.presentation.ui.screen.booksummary

import ua.headway.booksummary.presentation.ui.resources.Constants.ErrorCodes
import ua.headway.booksummary.domain.model.BookSummary
import ua.headway.booksummary.domain.model.BookSummary.SummaryPart

sealed class UiState {
    data object Loading: UiState()

    data class Data(
        val summaryParts: List<SummaryPart>,
        val bookCoverUrl: String,
        val currentPartIndex: Int,
        val currentAudioPositionMs: Float,
        val audioSpeedLevel: Float,
        val isAudioPlaying: Boolean,
        val isListeningModeEnabled: Boolean
    ): UiState() {
        val currentSummaryPart: SummaryPart
            get() = summaryParts[currentPartIndex]
        val partsTotal: Int
            get() = summaryParts.size
    }

    sealed class Error(val errorMsg: String): UiState() {
        // TODO: Move SummaryPartsAreOver to Info UiState - not Error!
        data object SummaryPartsAreOver: Error("That's it! Congratulations!")
        data object UnknownError: Error("Something unknown happened...but Trump will fix it...")
    }

    val asData: Data?
        get() = this as? Data
}

sealed class UiIntent {
    data object FetchBookSummary: UiIntent()
    data object ToggleAudio: UiIntent()
    data class ChangeAudioPosition(val offsetMs: Float): UiIntent()
    data class SetAudioPosition(val newAudioPositionMs: Float): UiIntent()
    data object ToggleAudioSpeed: UiIntent()
    data object GoNextPart: UiIntent()
    data object GoPreviousPart: UiIntent()
    data object ToggleSummaryMode: UiIntent()
}

sealed class UiResult {
    data object Loading: UiResult()

    sealed class Success: UiResult() {
        data class BookSummaryFetched(val bookSummary: BookSummary): Success()
        data class AudioToggled(val isAudioPlaying: Boolean): Success()
        data class AudioPositionChanged(val currentAudioPosition: Float): Success()
        data class AudioPositionSet(val currentAudioPosition: Float): Success()
        data class AudioSpeedChanged(val audioSpeedLevel: Float): Success()
        data class PartSkipped(val currentPartIndex: Int): Success()
        data class PartRewound(val currentPartIndex: Int): Success()
        data class SummaryModeToggled(val isListeningModeEnabled: Boolean): Success()
    }

    data class Failure(val errorCode: Int): UiResult() {
        fun toError(): UiState.Error = when (errorCode) {
            ErrorCodes.BookSummary.ERROR_SUMMARY_PARTS_ARE_OVER -> UiState.Error.SummaryPartsAreOver
            else -> UiState.Error.UnknownError
        }
    }
}