package ua.headway.booksummary.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import ua.headway.booksummary.data.di.source.DatabaseSource
import ua.headway.booksummary.data.di.source.MemorySource
import ua.headway.booksummary.data.di.source.NetworkSource
import ua.headway.booksummary.data.source.base.BookSource
import ua.headway.booksummary.domain.model.BookSummary
import ua.headway.booksummary.domain.repository.BookRepository
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    @MemorySource
    private val memoryBookSource: BookSource,
    @NetworkSource
    private val networkBookSource: BookSource,
    @DatabaseSource
    private val dbBookSource: BookSource
): BookRepository {
    override fun getBookSummaryById(bookId: Long): Flow<BookSummary> {
        return memoryBookSource.getBooks().mapNotNull { books ->
            books.firstOrNull { it.id == bookId }
        }
    }
}