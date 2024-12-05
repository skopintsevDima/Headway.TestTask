package ua.headway.booksummary.data.repository

import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import ua.headway.booksummary.data.di.source.DatabaseSource
import ua.headway.booksummary.data.di.source.MemorySource
import ua.headway.booksummary.data.di.source.NetworkSource
import ua.headway.booksummary.data.source.BookSource
import ua.headway.booksummary.domain.model.BookSummaryModel
import ua.headway.booksummary.domain.repository.BookRepository
import ua.headway.booksummary.domain.usecase.CheckInternetUseCase
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class BookRepositoryImpl(
    @MemorySource
    private val memoryBookSource: BookSource,
    @NetworkSource
    private val networkBookSource: BookSource,
    @DatabaseSource
    private val dbBookSource: BookSource,
    @ApplicationContext
    private val checkInternetUseCase: CheckInternetUseCase
): BookRepository {
    override fun getBookSummaryById(bookId: Long): Flow<BookSummaryModel> {
        val currentBookSource = networkBookSource.takeIf { isNetworkAvailable() }
            ?: memoryBookSource
        return flow {
            emitAll(currentBookSource.getBooks())
        }.catch { throwable ->
            if (isNetworkError(throwable)) {
                emitAll(memoryBookSource.getBooks())
            } else {
                throw throwable
            }
        }.mapNotNull { books ->
            books.firstOrNull { it.id == bookId }
        }
    }

    private fun isNetworkAvailable() = checkInternetUseCase.execute()

    private fun isNetworkError(throwable: Throwable): Boolean {
        return when (throwable) {
            is UnknownHostException,
            is ConnectException,
            is NoRouteToHostException,
            is SocketTimeoutException -> true
            else -> false
        }
    }
}