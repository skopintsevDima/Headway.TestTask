package ua.headway.booksummary.data.source.base

import kotlinx.coroutines.flow.Flow
import ua.headway.booksummary.domain.model.BookSummary

interface BookSource {
    fun getBooks(): Flow<List<BookSummary>>
}