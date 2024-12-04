package ua.headway.booksummary.domain.usecase

import ua.headway.booksummary.domain.model.BookSummary

interface GetBookSummaryUseCase {
    suspend fun execute(bookId: Long): BookSummary
}