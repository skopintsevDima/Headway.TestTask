package ua.headway.booksummary.presentation.ui.screen.booksummary

import androidx.lifecycle.ViewModel

class BookSummaryViewModel: ViewModel() {
    sealed class UiState {
        data class AudioSummary(val audioLink: String): UiState()
        data class TextSummary(val text: String): UiState()

        val isAudioMode: Boolean
            get() = this is AudioSummary
    }
}