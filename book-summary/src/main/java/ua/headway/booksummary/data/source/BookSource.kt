package ua.headway.booksummary.data.source

import kotlinx.coroutines.flow.Flow
import ua.headway.booksummary.domain.model.BookSummaryModel

interface BookSource {
    fun getBooks(): Flow<List<BookSummaryModel>>
}