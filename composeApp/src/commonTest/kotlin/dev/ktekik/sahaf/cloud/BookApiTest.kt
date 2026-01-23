package dev.ktekik.sahaf.cloud

import app.cash.turbine.test
import dev.ktekik.sahaf.models.Book
import dev.ktekik.sahaf.models.BookListing
import dev.ktekik.sahaf.models.Cover
import dev.ktekik.sahaf.models.CursorPagedListings
import dev.ktekik.sahaf.models.DeliveryMethod
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class BookApiTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

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

    private val testListings = CursorPagedListings(
        items = listOf(
            BookListing(
                readerId = Uuid.random(),
                book = testBook,
                createdAt = Instant.parse("2024-01-01T00:00:00Z"),
                updatedAt = Instant.parse("2024-01-02T00:00:00Z"),
                deliveryMethod = DeliveryMethod.Shipping,
                likes = setOf("user-1"),
                viewCount = 10,
                listingUuid = Uuid.random(),
                readersContacted = emptySet(),
                zipcode = "12345"
            )
        ),
        nextCursor = Instant.parse("2024-01-01T00:00:00Z")
    )

    private fun createMockClient(
        responseBody: String,
        statusCode: HttpStatusCode = HttpStatusCode.OK,
        requestValidator: ((io.ktor.client.engine.mock.MockRequestHandleScope, io.ktor.client.request.HttpRequestData) -> Unit)? = null
    ): HttpClient {
        val mockEngine = MockEngine { request ->
            requestValidator?.invoke(this, request)
            respond(
                content = responseBody,
                status = statusCode,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        return HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }
    }

    private fun createErrorMockClient(
        statusCode: HttpStatusCode,
        errorBody: String = "Error occurred"
    ): HttpClient {
        val mockEngine = MockEngine {
            respond(
                content = errorBody,
                status = statusCode,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        return HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }
    }

    private fun createExceptionMockClient(exception: Exception): HttpClient {
        val mockEngine = MockEngine {
            throw exception
        }

        return HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }
    }

    // ============ queryByIsbn Tests ============

    @Test
    fun `queryByIsbn returns Success when API returns 200`() = runTest {
        val mockClient = createMockClient(json.encodeToString(testBook))
        val bookApi = bookApiBuilder(mockClient, "http://test.com")

        bookApi.queryByIsbn("9780123456789").test {
            val result = awaitItem()
            assertTrue(result is ApiResult.Success)
            assertEquals(testBook.title, result.data.title)
            assertEquals(testBook.isbn, result.data.isbn)
            awaitComplete()
        }
    }

    @Test
    fun `queryByIsbn returns Error when API returns 404`() = runTest {
        val mockClient = createErrorMockClient(HttpStatusCode.NotFound, "Book not found")
        val bookApi = bookApiBuilder(mockClient, "http://test.com")

        bookApi.queryByIsbn("invalid-isbn").test {
            val result = awaitItem()
            assertTrue(result is ApiResult.Error)
            assertEquals(result.message?.contains("404"), true)
            awaitComplete()
        }
    }

    @Test
    fun `queryByIsbn returns Error when API returns 500`() = runTest {
        val mockClient = createErrorMockClient(HttpStatusCode.InternalServerError, "Server error")
        val bookApi = bookApiBuilder(mockClient, "http://test.com")

        bookApi.queryByIsbn("9780123456789").test {
            val result = awaitItem()
            assertTrue(result is ApiResult.Error)
            assertEquals(result.message?.contains("500"), true)
            awaitComplete()
        }
    }

    @Test
    fun `queryByIsbn returns Error when exception is thrown`() = runTest {
        val mockClient = createExceptionMockClient(RuntimeException("Network error"))
        val bookApi = bookApiBuilder(mockClient, "http://test.com")

        bookApi.queryByIsbn("9780123456789").test {
            val result = awaitItem()
            assertTrue(result is ApiResult.Error)
            assertEquals("Network error", result.message)
            awaitComplete()
        }
    }

    @Test
    fun `queryByIsbn calls correct URL`() = runTest {
        var capturedUrl: String? = null
        val mockEngine = MockEngine { request ->
            capturedUrl = request.url.toString()
            respond(
                content = json.encodeToString(testBook),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val mockClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }

        val bookApi = bookApiBuilder(mockClient, "http://test.com")
        bookApi.queryByIsbn("9780123456789").test {
            awaitItem()
            awaitComplete()
        }

        assertEquals("http://test.com/isbn/9780123456789", capturedUrl)
    }

    // ============ getListingsWithShipping Tests ============

    @Test
    fun `getListingsWithShipping returns Success when API returns 200`() = runTest {
        val mockClient = createMockClient(json.encodeToString(testListings))
        val bookApi = bookApiBuilder(mockClient, "http://test.com")

        bookApi.getListingsWithShipping(
            readerId = "reader-123",
            excludeSelf = false
        ).test {
            val result = awaitItem()
            assertTrue(result is ApiResult.Success)
            assertEquals(1, result.data.items.size)
            awaitComplete()
        }
    }

    @Test
    fun `getListingsWithShipping returns Error when API returns 401`() = runTest {
        val mockClient = createErrorMockClient(HttpStatusCode.Unauthorized, "Unauthorized")
        val bookApi = bookApiBuilder(mockClient, "http://test.com")

        bookApi.getListingsWithShipping(
            readerId = "reader-123",
            excludeSelf = false
        ).test {
            val result = awaitItem()
            assertTrue(result is ApiResult.Error)
            assertEquals(result.message?.contains("401"), true)
            awaitComplete()
        }
    }

    @Test
    fun `getListingsWithShipping returns Error when exception is thrown`() = runTest {
        val mockClient = createExceptionMockClient(RuntimeException("Connection timeout"))
        val bookApi = bookApiBuilder(mockClient, "http://test.com")

        bookApi.getListingsWithShipping(
            readerId = "reader-123",
            excludeSelf = false
        ).test {
            val result = awaitItem()
            assertTrue(result is ApiResult.Error)
            assertEquals("Connection timeout", result.message)
            awaitComplete()
        }
    }

    @Test
    fun `getListingsWithShipping sends correct parameters`() = runTest {
        var capturedUrl: String? = null
        val mockEngine = MockEngine { request ->
            capturedUrl = request.url.toString()
            respond(
                content = json.encodeToString(testListings),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val mockClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }

        val bookApi = bookApiBuilder(mockClient, "http://test.com")
        bookApi.getListingsWithShipping(
            readerId = "reader-123",
            excludeSelf = true,
            limit = 10
        ).test {
            awaitItem()
            awaitComplete()
        }

        assertEquals(capturedUrl?.contains("readerId=reader-123"), true)
        assertEquals(capturedUrl?.contains("excludeSelf=true"), true)
        assertEquals(capturedUrl?.contains("limit=10"), true)
    }

    @Test
    fun `getListingsWithShipping sends before parameter when provided`() = runTest {
        var capturedUrl: String? = null
        val mockEngine = MockEngine { request ->
            capturedUrl = request.url.toString()
            respond(
                content = json.encodeToString(testListings),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val mockClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }

        val beforeTime = Instant.parse("2024-01-01T00:00:00Z")
        val bookApi = bookApiBuilder(mockClient, "http://test.com")
        bookApi.getListingsWithShipping(
            readerId = "reader-123",
            excludeSelf = false,
            before = beforeTime
        ).test {
            awaitItem()
            awaitComplete()
        }

        assertEquals(capturedUrl?.contains("before="), true)
    }

    @Test
    fun `getListingsWithShipping does not send limit when null`() = runTest {
        var capturedUrl: String? = null
        val mockEngine = MockEngine { request ->
            capturedUrl = request.url.toString()
            respond(
                content = json.encodeToString(testListings),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val mockClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }

        val bookApi = bookApiBuilder(mockClient, "http://test.com")
        bookApi.getListingsWithShipping(
            readerId = "reader-123",
            excludeSelf = false,
            limit = null
        ).test {
            awaitItem()
            awaitComplete()
        }

        assertEquals(capturedUrl?.contains("limit="), false)
    }

    @Test
    fun `getListingsWithShipping uses default excludeSelf true`() = runTest {
        var capturedUrl: String? = null
        val mockEngine = MockEngine { request ->
            capturedUrl = request.url.toString()
            respond(
                content = json.encodeToString(testListings),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val mockClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }

        val bookApi = bookApiBuilder(mockClient, "http://test.com")
        // Using default excludeSelf
        bookApi.getListingsWithShipping(readerId = "reader-123").test {
            awaitItem()
            awaitComplete()
        }

        assertEquals(capturedUrl?.contains("excludeSelf=true"), true)
    }

    @Test
    fun `getListingsWithShipping returns empty list when no listings`() = runTest {
        val emptyListings = CursorPagedListings(items = emptyList(), nextCursor = null)
        val mockClient = createMockClient(json.encodeToString(emptyListings))
        val bookApi = bookApiBuilder(mockClient, "http://test.com")

        bookApi.getListingsWithShipping(
            readerId = "reader-123",
            excludeSelf = false
        ).test {
            val result = awaitItem()
            assertTrue(result is ApiResult.Success)
            assertTrue(result.data.items.isEmpty())
            assertEquals(null, result.data.nextCursor)
            awaitComplete()
        }
    }

    // ============ bookApiBuilder Tests ============

    @Suppress("USELESS_IS_CHECK")
    @Test
    fun `bookApiBuilder creates BookApi instance`() {
        val mockEngine = MockEngine { respond("", HttpStatusCode.OK) }
        val mockClient = HttpClient(mockEngine)

        val bookApi = bookApiBuilder(mockClient, "http://test.com")

        assertTrue(bookApi is BookApi)
    }

    @Test
    fun `bookApiBuilder uses default baseUrl when not provided`() = runTest {
        var capturedUrl: String? = null
        val mockEngine = MockEngine { request ->
            capturedUrl = request.url.toString()
            respond(
                content = json.encodeToString(testBook),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val mockClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }

        val bookApi = bookApiBuilder(mockClient)
        bookApi.queryByIsbn("test-isbn").test {
            awaitItem()
            awaitComplete()
        }

        assertEquals(capturedUrl?.startsWith("http://192.168.68.67:8080"), true)
    }
}
