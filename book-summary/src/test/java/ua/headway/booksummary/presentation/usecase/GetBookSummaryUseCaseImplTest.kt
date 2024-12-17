package ua.headway.booksummary.presentation.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import ua.headway.booksummary.domain.model.BookSummaryModel
import ua.headway.booksummary.domain.repository.BookRepository

class GetBookSummaryUseCaseImplTest {
    private val mockBookRepository: BookRepository = mockk()
    private lateinit var getBookSummaryUseCaseImpl: GetBookSummaryUseCaseImpl

    private val testBookSummary = BookSummaryModel(
        1234L,
        listOf(BookSummaryModel.SummaryPart("desc1", "audioUrl1", "text1")),
        "bookCoverUrl"
    )

    @Before
    fun setup() {
        getBookSummaryUseCaseImpl = GetBookSummaryUseCaseImpl(mockBookRepository)
    }

    @Test
    fun `fetches book summary correctly`() = runTest {
        coEvery {
            mockBookRepository.getBookSummaryById(1234L)
        } returns flowOf(testBookSummary)

        val result = getBookSummaryUseCaseImpl.execute(1234L)

        coVerify { mockBookRepository.getBookSummaryById(1234L) }
        assertEquals(testBookSummary, result)
    }

    @Test
    fun `handles empty response or no matching bookId in the repository`() = runTest {
        coEvery { mockBookRepository.getBookSummaryById(9999L) } returns flowOf()

        val result = getBookSummaryUseCaseImpl.execute(9999L)

        coVerify { mockBookRepository.getBookSummaryById(9999L) }
        assertNull(result)
    }
}