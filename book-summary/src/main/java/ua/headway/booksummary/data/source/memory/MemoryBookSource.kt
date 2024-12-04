package ua.headway.booksummary.data.source.memory

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ua.headway.booksummary.data.source.base.BookSource
import ua.headway.booksummary.domain.model.BookSummary

class MemoryBookSource : BookSource {
    override fun getBooks(): Flow<List<BookSummary>> {
        return flowOf(listOf(bookSummary1, bookSummary2, bookSummary3))
    }

    companion object {
        // TODO: Replace audio URLs for Book2/Book3
        private val bookSummary1 = BookSummary(
            id = 1,
            summaryParts = listOf(
                BookSummary.SummaryPart(
                    description = "BOOK1. Summary part 1",
                    audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/BabyElephantWalk60.wav",
                    text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum",
                ),
                BookSummary.SummaryPart(
                    description = "BOOK1. Summary part 2",
                    audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/StarWars60.wav",
                    text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum",
                ),
                BookSummary.SummaryPart(
                    description = "BOOK1. Summary part 3",
                    audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/CantinaBand60.wav",
                    text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum",
                ),
                BookSummary.SummaryPart(
                    description = "BOOK1. Summary part 4",
                    audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/ImperialMarch60.wav",
                    text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum",
                ),
                BookSummary.SummaryPart(
                    description = "BOOK1. Summary part 5",
                    audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/PinkPanther60.wav",
                    text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum",
                ),
            ),
            bookCoverUrl = "https://picsum.photos/id/24/1080/1920",
        )

        private val bookSummary2 = BookSummary(
            id = 2,
            summaryParts = listOf(
                BookSummary.SummaryPart(
                    description = "BOOK2. Summary part 1",
                    audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/BabyElephantWalk60.wav",
                    text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum",
                ),
                BookSummary.SummaryPart(
                    description = "BOOK2. Summary part 2",
                    audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/StarWars60.wav",
                    text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum",
                ),
                BookSummary.SummaryPart(
                    description = "BOOK2. Summary part 3",
                    audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/CantinaBand60.wav",
                    text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum",
                ),
                BookSummary.SummaryPart(
                    description = "BOOK2. Summary part 4",
                    audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/ImperialMarch60.wav",
                    text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum",
                ),
                BookSummary.SummaryPart(
                    description = "BOOK2. Summary part 5",
                    audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/PinkPanther60.wav",
                    text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum",
                ),
            ),
            bookCoverUrl = "https://picsum.photos/id/25/1080/1920",
        )

        private val bookSummary3 = BookSummary(
            id = 3,
            summaryParts = listOf(
                BookSummary.SummaryPart(
                    description = "BOOK3. Summary part 1",
                    audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/BabyElephantWalk60.wav",
                    text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum",
                ),
                BookSummary.SummaryPart(
                    description = "BOOK3. Summary part 2",
                    audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/StarWars60.wav",
                    text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum",
                ),
                BookSummary.SummaryPart(
                    description = "BOOK3. Summary part 3",
                    audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/CantinaBand60.wav",
                    text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum",
                ),
                BookSummary.SummaryPart(
                    description = "BOOK3. Summary part 4",
                    audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/ImperialMarch60.wav",
                    text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum",
                ),
                BookSummary.SummaryPart(
                    description = "BOOK3. Summary part 5",
                    audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/PinkPanther60.wav",
                    text = "Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum Lorem impsum",
                ),
            ),
            bookCoverUrl = "https://picsum.photos/id/26/1080/1920",
        )
    }
}