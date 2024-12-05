package ua.headway.booksummary.domain.usecase

import ua.headway.booksummary.domain.model.BookSummaryModel

interface GetBookSummaryUseCase {
    suspend fun execute(bookId: Long): BookSummaryModel
}