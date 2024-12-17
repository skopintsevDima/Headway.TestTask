package ua.headway.booksummary.presentation.usecase

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import ua.headway.booksummary.domain.model.BookSummaryModel
import ua.headway.booksummary.domain.repository.BookRepository
import ua.headway.booksummary.domain.usecase.GetBookSummaryUseCase

class GetBookSummaryUseCaseImpl(
    private val bookRepository: BookRepository
): GetBookSummaryUseCase {
    override suspend fun execute(bookId: Long): BookSummaryModel? {
        delay(2000) // Imitate real data fetching
        return bookRepository.getBookSummaryById(bookId).firstOrNull()
    }
}