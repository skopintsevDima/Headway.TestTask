package ua.headway.booksummary.domain.repository

import kotlinx.coroutines.flow.Flow
import ua.headway.booksummary.domain.model.BookSummaryModel

interface BookRepository {
    fun getBookSummaryById(bookId: Long): Flow<BookSummaryModel>
}