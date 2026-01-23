package dev.ktekik.sahaf.listing

import dev.ktekik.sahaf.cloud.ApiResult
import dev.ktekik.sahaf.cloud.BookApi
import dev.ktekik.sahaf.models.Book
import dev.ktekik.sahaf.models.Cover
import dev.ktekik.sahaf.models.CursorPagedListings
import dev.ktekik.sahaf.usecases.IsbnQueryUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.orbitmvi.orbit.test.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BookListingViewModelTest {

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
    fun `initial state should be Loading`() = runTest(UnconfinedTestDispatcher()) {
        val viewModel = createViewModel(bookResult = ApiResult.Success(testBook))

        viewModel.test(this) {
            assertEquals(BookListingScreenState.Loading, viewModel.container.stateFlow.value)
        }
    }

    @Test
    fun `fetchBook should transition to Ready state on success`() = runTest {
        val viewModel = createViewModel(bookResult = ApiResult.Success(testBook))

        viewModel.test(this) {
            containerHost.fetchBook("9780123456789")
            
            val state = awaitState()
            assertTrue(state is BookListingScreenState.Ready)
            assertEquals(testBook, state.book)
        }
    }

    @Test
    fun `fetchBook should transition to Error state on failure`() = runTest {
        val errorMessage = "Book not found"
        val viewModel = createViewModel(bookResult = ApiResult.Error(errorMessage))

        viewModel.test(this) {

            containerHost.fetchBook("invalid-isbn")
            
            val state = awaitState()
            assertTrue(state is BookListingScreenState.Error)
            assertEquals(errorMessage, state.message)
        }
    }

    @Test
    fun `Ready state should contain correct book details`() = runTest {
        val viewModel = createViewModel(bookResult = ApiResult.Success(testBook))

        viewModel.test(this) {

            containerHost.fetchBook("9780123456789")
            
            val state = awaitState() as BookListingScreenState.Ready
            assertEquals("Test Book", state.book.title)
            assertEquals("9780123456789", state.book.isbn)
            assertTrue(state.book.authors.contains("Author One"))
            assertEquals(300, state.book.pageCount)
            assertEquals("2023-01-01", state.book.publishedDate)
        }
    }

    @Test
    fun `fetchBook should pass correct ISBN to use case`() = runTest {
        var capturedIsbn: String? = null
        val fakeBookApi = object : BookApi {
            override suspend fun getListingsWithShipping(
                readerId: String,
                excludeSelf: Boolean,
                limit: Int?,
                before: Instant?
            ): Flow<ApiResult<CursorPagedListings>> = flowOf(ApiResult.Error("Not implemented"))

            override suspend fun queryByIsbn(isbn: String): Flow<ApiResult<Book>> {
                capturedIsbn = isbn
                return flowOf(ApiResult.Success(testBook))
            }
        }

        val viewModel = BookListingViewModel(
            isbnQueryUseCase = IsbnQueryUseCase(fakeBookApi)
        )

        viewModel.test(this) {
            containerHost.fetchBook("9780123456789")
            
            awaitState()
            assertEquals("9780123456789", capturedIsbn)
        }
    }

    @Test
    fun `book with null cover should still work`() = runTest {
        val bookWithNullCover = testBook.copy(cover = null)
        val viewModel = createViewModel(bookResult = ApiResult.Success(bookWithNullCover))

        viewModel.test(this) {

            containerHost.fetchBook("9780123456789")
            
            val state = awaitState() as BookListingScreenState.Ready
            assertEquals(null, state.book.cover)
        }
    }

    @Test
    fun `book with empty authors should still work`() = runTest {
        val bookWithNoAuthors = testBook.copy(authors = emptySet())
        val viewModel = createViewModel(bookResult = ApiResult.Success(bookWithNoAuthors))

        viewModel.test(this) {

            containerHost.fetchBook("9780123456789")
            
            val state = awaitState() as BookListingScreenState.Ready
            assertTrue(state.book.authors.isEmpty())
        }
    }

    @Test
    fun `Error state should contain error message`() = runTest {
        val errorMessage = "Network connection failed"
        val viewModel = createViewModel(bookResult = ApiResult.Error(errorMessage))

        viewModel.test(this) {
            containerHost.fetchBook("9780123456789")
            
            val state = awaitState() as BookListingScreenState.Error
            assertEquals("Network connection failed", state.message)
        }
    }

    @Test
    fun `multiple fetchBook calls should update state accordingly`() = runTest {
        val firstBook = testBook.copy(title = "First Book")
        val secondBook = testBook.copy(title = "Second Book")
        
        var callCount = 0
        val fakeBookApi = object : BookApi {
            override suspend fun getListingsWithShipping(
                readerId: String,
                excludeSelf: Boolean,
                limit: Int?,
                before: Instant?
            ): Flow<ApiResult<CursorPagedListings>> = flowOf(ApiResult.Error("Not implemented"))

            override suspend fun queryByIsbn(isbn: String): Flow<ApiResult<Book>> {
                callCount++
                return flowOf(ApiResult.Success(if (callCount == 1) firstBook else secondBook))
            }
        }

        val viewModel = BookListingViewModel(
            isbnQueryUseCase = IsbnQueryUseCase(fakeBookApi)
        )

        viewModel.test(this) {
            containerHost.fetchBook("isbn-1")
            var state = awaitState() as BookListingScreenState.Ready
            assertEquals("First Book", state.book.title)
            
            containerHost.fetchBook("isbn-2")
            state = awaitState() as BookListingScreenState.Ready
            assertEquals("Second Book", state.book.title)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun createViewModel(
        bookResult: ApiResult<Book>
    ): BookListingViewModel {
        val fakeBookApi = FakeBookApi(bookResult)
        return BookListingViewModel(
            isbnQueryUseCase = IsbnQueryUseCase(fakeBookApi),
            dispatcher = UnconfinedTestDispatcher()
        )
    }
}

// Test double
private class FakeBookApi(
    private val bookResult: ApiResult<Book>
) : BookApi {
    override suspend fun getListingsWithShipping(
        readerId: String,
        excludeSelf: Boolean,
        limit: Int?,
        before: Instant?
    ): Flow<ApiResult<CursorPagedListings>> = flowOf(ApiResult.Error("Not implemented"))

    override suspend fun queryByIsbn(isbn: String): Flow<ApiResult<Book>> = flowOf(bookResult)
}
