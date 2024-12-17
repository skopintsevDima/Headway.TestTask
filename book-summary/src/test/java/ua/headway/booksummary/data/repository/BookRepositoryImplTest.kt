package ua.headway.booksummary.data.repository

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import ua.headway.booksummary.data.source.BookSource
import ua.headway.booksummary.domain.model.BookSummaryModel
import ua.headway.booksummary.domain.usecase.CheckInternetUseCase
import java.net.UnknownHostException

class BookRepositoryImplTest {
    private val mockMemoryBookSource: BookSource = mockk()
    private val mockNetworkBookSource: BookSource = mockk()
    private val mockDbBookSource: BookSource = mockk()
    private val mockCheckInternetUseCase: CheckInternetUseCase = mockk()

    private lateinit var bookRepositoryImpl: BookRepositoryImpl

    private val testBookSummary = BookSummaryModel(
        1234L,
        listOf(
            BookSummaryModel.SummaryPart("desc1", "audioUrl1", "text1"),
            BookSummaryModel.SummaryPart("desc2", "audioUrl2", "text2"),
            BookSummaryModel.SummaryPart("desc3", "audioUrl3", "text3")
        ),
        "bookCoverUrl"
    )

    @Before
    fun setup() {
        bookRepositoryImpl = BookRepositoryImpl(
            memoryBookSource = mockMemoryBookSource,
            networkBookSource = mockNetworkBookSource,
            dbBookSource = mockDbBookSource,
            checkInternetUseCase = mockCheckInternetUseCase
        )
    }

    @Test
    fun `getBookSummaryById fetches book summary correctly`() = runTest {
        every { mockCheckInternetUseCase.execute() } returns true
        every { mockNetworkBookSource.getBooks() } returns flowOf(listOf(testBookSummary))
        every { mockMemoryBookSource.getBooks() } returns flowOf(emptyList())

        val result = bookRepositoryImpl.getBookSummaryById(1234L).firstOrNull()

        assertEquals(testBookSummary, result)
    }

    @Test
    fun `getBookSummaryById uses network source when network is available`() = runTest {
        every { mockCheckInternetUseCase.execute() } returns true
        every { mockNetworkBookSource.getBooks() } returns flowOf(listOf(testBookSummary))
        every { mockMemoryBookSource.getBooks() } returns flowOf(emptyList())

        val result = bookRepositoryImpl.getBookSummaryById(1234L).firstOrNull()

        verify { mockNetworkBookSource.getBooks() }
        verify(exactly = 0) { mockMemoryBookSource.getBooks() }
        assertEquals(testBookSummary, result)
    }

    @Test
    fun `getBookSummaryById uses memory source when network is not available`() = runTest {
        every { mockCheckInternetUseCase.execute() } returns false
        every { mockMemoryBookSource.getBooks() } returns flowOf(listOf(testBookSummary))

        val result = bookRepositoryImpl.getBookSummaryById(1234L).firstOrNull()

        verify { mockMemoryBookSource.getBooks() }
        verify(exactly = 0) { mockNetworkBookSource.getBooks() }
        assertEquals(testBookSummary, result)
    }

    @Test
    fun `getBookSummaryById falls back to memory source on network failure`() = runTest {
        every { mockCheckInternetUseCase.execute() } returns true
        every { mockNetworkBookSource.getBooks() } throws UnknownHostException("Network error")
        every { mockMemoryBookSource.getBooks() } returns flowOf(listOf(testBookSummary))

        val result = bookRepositoryImpl.getBookSummaryById(1234L).firstOrNull()

        verify { mockNetworkBookSource.getBooks() }
        verify { mockMemoryBookSource.getBooks() }
        assertEquals(testBookSummary, result)
    }

    @Test
    fun `getBookSummaryById uses memory source when network source is empty`() = runTest {
        every { mockCheckInternetUseCase.execute() } returns true
        every { mockNetworkBookSource.getBooks() } returns flowOf()
        every { mockMemoryBookSource.getBooks() } returns flowOf(listOf(testBookSummary))

        val result = bookRepositoryImpl.getBookSummaryById(1234L).firstOrNull()

        verify { mockNetworkBookSource.getBooks() }
        verify { mockMemoryBookSource.getBooks() }
        assertEquals(testBookSummary, result)
    }

    @Test
    fun `getBookSummaryById uses memory source when network source provides empty books list`() = runTest {
        every { mockCheckInternetUseCase.execute() } returns true
        every { mockNetworkBookSource.getBooks() } returns flowOf(emptyList())
        every { mockMemoryBookSource.getBooks() } returns flowOf(listOf(testBookSummary))

        val result = bookRepositoryImpl.getBookSummaryById(1234L).firstOrNull()

        verify { mockNetworkBookSource.getBooks() }
        verify { mockMemoryBookSource.getBooks() }
        assertEquals(testBookSummary, result)
    }

    @Test
    fun `getBookSummaryById returns null when no book is found`() = runTest {
        every { mockCheckInternetUseCase.execute() } returns true
        every { mockNetworkBookSource.getBooks() } returns flowOf()
        every { mockMemoryBookSource.getBooks() } returns flowOf()

        val result = bookRepositoryImpl.getBookSummaryById(1234L).firstOrNull()

        assertNull(result)
    }
}