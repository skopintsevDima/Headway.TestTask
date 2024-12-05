package ua.headway.booksummary.data.api

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ua.headway.booksummary.data.mapper.toDomain
import ua.headway.booksummary.data.source.BookSource
import ua.headway.booksummary.domain.model.BookSummaryModel
import java.io.IOException

class ApiBookSource(
    @ApplicationContext
    private val context: Context
): BookSource {
    override fun getBooks(): Flow<List<BookSummaryModel>> {
        val books = mutableListOf<BookSummaryModel>()
        loadBookSummaries(context)?.map { it.toDomain() }?.let { newBooks ->
            books.addAll(newBooks)
        }
        return flowOf(books)
    }

    // For testing purpose only!!!
    private fun loadBookSummaries(context: Context): List<BookSummaryResponse>? {
        val jsonString = context.loadJsonFromAssets("book_summaries_sample.json")
        if (jsonString != null) {
            val moshi = Moshi.Builder().build()
            val type = Types.newParameterizedType(List::class.java, BookSummaryResponse::class.java)
            val adapter: JsonAdapter<List<BookSummaryResponse>> = moshi.adapter(type)
            return adapter.fromJson(jsonString)
        }
        return null
    }

    // For testing purpose only!!!
    private fun Context.loadJsonFromAssets(fileName: String): String? {
        return try {
            val inputStream = assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}