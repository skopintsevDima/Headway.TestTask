package ua.headway.booksummary.data.db

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ua.headway.booksummary.data.source.BookSource
import ua.headway.booksummary.domain.model.BookSummaryModel

class DbBookSource : BookSource {
    override fun getBooks(): Flow<List<BookSummaryModel>> {
        return flowOf(emptyList())
    }
}