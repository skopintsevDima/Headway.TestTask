package ua.headway.booksummary.data.source.db

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ua.headway.booksummary.data.source.base.BookSource
import ua.headway.booksummary.domain.model.BookSummary

class DbBookSource : BookSource {
    override fun getBooks(): Flow<List<BookSummary>> {
        return flowOf(emptyList())
    }
}