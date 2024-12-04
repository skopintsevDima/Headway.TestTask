package ua.headway.booksummary.presentation.usecase

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import ua.headway.booksummary.domain.model.BookSummary
import ua.headway.booksummary.domain.repository.BookRepository
import ua.headway.booksummary.domain.usecase.GetBookSummaryUseCase

class GetBookSummaryUseCaseImpl(
    private val bookRepository: BookRepository
): GetBookSummaryUseCase {
    override suspend fun execute(bookId: Long): BookSummary {
        delay(2000) // TODO: Replace with real data fetching (from API/DB)
        return bookRepository.getBookSummaryById(bookId).first()
    }
}