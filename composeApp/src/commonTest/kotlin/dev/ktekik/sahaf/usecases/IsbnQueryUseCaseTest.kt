package dev.ktekik.sahaf.usecases

import app.cash.turbine.test
import dev.ktekik.sahaf.cloud.ApiResult
import dev.ktekik.sahaf.cloud.BookApi
import dev.ktekik.sahaf.models.Book
import dev.ktekik.sahaf.models.Cover
import dev.ktekik.sahaf.models.CursorPagedListings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IsbnQueryUseCaseTest {

    private val testBook = Book(
        bookId = "book-123",
        isbn = "9780123456789",
        infoLink = "https://example.com/book",
        title = "Test Book",
        authors = setOf("Author One", "Author Two"),
        pageCount = 300,
        publishedDate = "2023-01-01",
        publishers = setOf("Test Publisher"),
        subjects = setOf("Fiction", "Science"),
        cover = Cover(
            small = "https://example.com/small.jpg",
            medium = "https://example.com/medium.jpg",
            large = "https://example.com/large.jpg"
        ),
        snippets = setOf("This is a great book...")
    )

    @Test
    fun `execute returns Success when API returns book`() = runTest {
        val queryISBNAPI = QueryIsbnApi(ApiResult.Success(testBook))
        val useCase = IsbnQueryUseCase(queryISBNAPI)

        useCase.execute("9780123456789").test {
            val result = awaitItem()
            assertTrue(result is IsbnQueryResult.Success)
            assertEquals(testBook, result.book)
            awaitComplete()
        }
    }

    @Test
    fun `execute returns Error when API returns error`() = runTest {
        val errorMessage = "Book not found"
        val queryISBNAPI = QueryIsbnApi(ApiResult.Error(errorMessage))
        val useCase = IsbnQueryUseCase(queryISBNAPI)

        useCase.execute("invalid-isbn").test {
            val result = awaitItem()
            assertTrue(result is IsbnQueryResult.Error)
            assertEquals(errorMessage, result.message)
            awaitComplete()
        }
    }

    @Test
    fun `execute returns Unknown error when API error message is null`() = runTest {
        val queryISBNAPI = QueryIsbnApi(ApiResult.Error(null))
        val useCase = IsbnQueryUseCase(queryISBNAPI)

        useCase.execute("invalid-isbn").test {
            val result = awaitItem()
            assertTrue(result is IsbnQueryResult.Error)
            assertEquals("Unknown error", result.message)
            awaitComplete()
        }
    }

    @Test
    fun `execute passes correct ISBN to API`() = runTest {
        var capturedIsbn: String? = null
        val fakeBookApi = object : BookApi {
            override suspend fun queryByIsbn(isbn: String): Flow<ApiResult<Book>> {
                capturedIsbn = isbn
                return flowOf(ApiResult.Success(testBook))
            }

            override suspend fun getListingsWithShipping(
                readerId: String,
                excludeSelf: Boolean,
                limit: Int?,
                before: Instant?
            ): Flow<ApiResult<CursorPagedListings>> = flowOf(ApiResult.Error("Not implemented"))
        }

        val useCase = IsbnQueryUseCase(fakeBookApi)
        useCase.execute("9780123456789").test {
            awaitItem()
            awaitComplete()
        }

        assertEquals("9780123456789", capturedIsbn)
    }

    @Test
    fun `Success result contains correct book details`() = runTest {
        val queryISBNAPI = QueryIsbnApi(ApiResult.Success(testBook))
        val useCase = IsbnQueryUseCase(queryISBNAPI)

        useCase.execute("9780123456789").test {
            val result = awaitItem() as IsbnQueryResult.Success
            assertEquals("Test Book", result.book.title)
            assertEquals("9780123456789", result.book.isbn)
            assertEquals(2, result.book.authors.size)
            assertTrue(result.book.authors.contains("Author One"))
            assertEquals(300, result.book.pageCount)
            awaitComplete()
        }
    }

    @Test
    fun `execute handles book with null cover`() = runTest {
        val bookWithNullCover = testBook.copy(cover = null)
        val queryISBNAPI = QueryIsbnApi(ApiResult.Success(bookWithNullCover))
        val useCase = IsbnQueryUseCase(queryISBNAPI)

        useCase.execute("9780123456789").test {
            val result = awaitItem() as IsbnQueryResult.Success
            assertEquals(null, result.book.cover)
            awaitComplete()
        }
    }

    @Test
    fun `execute handles book with empty authors`() = runTest {
        val bookWithNoAuthors = testBook.copy(authors = emptySet())
        val queryISBNAPI = QueryIsbnApi(ApiResult.Success(bookWithNoAuthors))
        val useCase = IsbnQueryUseCase(queryISBNAPI)

        useCase.execute("9780123456789").test {
            val result = awaitItem() as IsbnQueryResult.Success
            assertTrue(result.book.authors.isEmpty())
            awaitComplete()
        }
    }
}

// Test double
private class QueryIsbnApi(
    private val queryResult: ApiResult<Book>
) : BookApi {
    override suspend fun queryByIsbn(isbn: String): Flow<ApiResult<Book>> = flowOf(queryResult)

    override suspend fun getListingsWithShipping(
        readerId: String,
        excludeSelf: Boolean,
        limit: Int?,
        before: Instant?
    ): Flow<ApiResult<CursorPagedListings>> = flowOf(ApiResult.Error("Not implemented"))
}
