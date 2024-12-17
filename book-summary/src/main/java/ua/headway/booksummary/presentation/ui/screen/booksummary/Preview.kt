package ua.headway.booksummary.presentation.ui.screen.booksummary

import android.content.Context
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.media3.common.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ua.headway.booksummary.domain.interactor.AudioPlaybackInteractor
import ua.headway.booksummary.domain.model.BookSummaryModel
import ua.headway.booksummary.domain.usecase.GetBookSummaryUseCase
import ua.headway.booksummary.presentation.manager.AudioPlaybackInteractorImpl
import ua.headway.booksummary.presentation.manager.PlayerSetupManager
import ua.headway.booksummary.presentation.ui.screen.booksummary.mapper.DefaultBookSummaryUiStateMapper
import ua.headway.core.presentation.ui.resources.provider.ResourceProvider

internal class MockBookSummaryViewModelWithState(
    state: UiState,
    resourceProvider: ResourceProvider = object : ResourceProvider {
        override fun getString(resId: Int): String = ""

        override fun getString(resId: Int, vararg formatArgs: Any): String = ""
    }
): BookSummaryViewModel(
    getBookSummaryUseCase = object : GetBookSummaryUseCase {
        override suspend fun execute(bookId: Long): BookSummaryModel {
            return BookSummaryModel(
                id = 0L,
                summaryParts = emptyList(),
                bookCoverUrl = ""
            )
        }
    },
    audioPlaybackInteractor = AudioPlaybackInteractorImpl(),
    playerSetupManager = object : PlayerSetupManager {
        override fun setupPlayer(
            audioPlaybackInteractor: AudioPlaybackInteractor,
            context: Context,
            audioItems: List<MediaItem>,
            onSuccess: () -> Unit,
            onFailure: (Throwable) -> Unit
        ) {
            // Do nothing
        }
    },
    uiStateMapper = DefaultBookSummaryUiStateMapper(resourceProvider),
    resourceProvider = resourceProvider,
    backgroundOpsDispatcher = Dispatchers.Default
) {
    override val uiState: StateFlow<UiState> = MutableStateFlow(state)
}

internal object MockUiState {
    val Idle = UiState.Idle
    val Loading = UiState.Loading
    val ErrorLoadBook = UiState.Error.LoadBookDataError("Can't load the book data")
    val ErrorUnknown = UiState.Error.UnknownError("An unknown error occurred")
    val DataListening = UiState.Data(
        summaryParts = listOf(
            BookSummaryModel.SummaryPart(
                description = "BOOK1. Summary part 1",
                audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/BabyElephantWalk60.wav",
                text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum ",
            ),
            BookSummaryModel.SummaryPart(
                description = "BOOK1. Summary part 2",
                audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/StarWars60.wav",
                text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum ",
            ),
            BookSummaryModel.SummaryPart(
                description = "BOOK1. Summary part 3",
                audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/CantinaBand60.wav",
                text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum ",
            ),
            BookSummaryModel.SummaryPart(
                description = "BOOK1. Summary part 4",
                audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/ImperialMarch60.wav",
                text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum ",
            ),
            BookSummaryModel.SummaryPart(
                description = "BOOK1. Summary part 5",
                audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/PinkPanther60.wav",
                text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum ",
            ),
        ),
        bookCoverUrl = "https://picsum.photos/id/24/1080/1920",
        currentPartIndex = 0,
        isPlayerReady = true,
        isAudioPlaying = false,
        currentAudioPositionMs = 35000,
        currentAudioDurationMs = 100000,
        audioSpeedLevel = 1.5f,
        isListeningModeEnabled = true
    )
    val DataReading = UiState.Data(
        summaryParts = listOf(
            BookSummaryModel.SummaryPart(
                description = "BOOK1. Summary part 1",
                audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/BabyElephantWalk60.wav",
                text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum ",
            ),
            BookSummaryModel.SummaryPart(
                description = "BOOK1. Summary part 2",
                audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/StarWars60.wav",
                text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum ",
            ),
            BookSummaryModel.SummaryPart(
                description = "BOOK1. Summary part 3",
                audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/CantinaBand60.wav",
                text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum ",
            ),
            BookSummaryModel.SummaryPart(
                description = "BOOK1. Summary part 4",
                audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/ImperialMarch60.wav",
                text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum ",
            ),
            BookSummaryModel.SummaryPart(
                description = "BOOK1. Summary part 5",
                audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/PinkPanther60.wav",
                text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum ",
            ),
        ),
        bookCoverUrl = "https://picsum.photos/id/24/1080/1920",
        currentPartIndex = 2,
        isPlayerReady = true,
        isAudioPlaying = false,
        currentAudioPositionMs = 35000,
        currentAudioDurationMs = 100000,
        audioSpeedLevel = 1.5f,
        isListeningModeEnabled = false
    )
}

internal class ErrorStatePreviewProvider : PreviewParameterProvider<UiState.Error> {
    override val values = sequenceOf(
        MockUiState.ErrorUnknown,
        MockUiState.ErrorLoadBook
    )
}

internal class DataStatePreviewProvider : PreviewParameterProvider<UiState.Data> {
    override val values = sequenceOf(
        MockUiState.DataListening,
        MockUiState.DataReading
    )
}