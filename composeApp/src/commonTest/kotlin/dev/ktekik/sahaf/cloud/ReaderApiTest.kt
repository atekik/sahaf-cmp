package dev.ktekik.sahaf.cloud

import app.cash.turbine.test
import dev.ktekik.sahaf.models.Reader
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ReaderApiTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val testReaderId = Uuid.random()
    private val testReader = Reader(
        readerId = testReaderId,
        name = "Test Reader",
        emailRelay = "test@example.com",
        pictureURL = "https://example.com/pic.jpg",
        activeListings = setOf("listing-1", "listing-2"),
        zipcode = "12345",
        avgRating = 4.5,
        followers = setOf("follower-1"),
        following = setOf("following-1"),
        geofenceFiftyKms = setOf("geo-1"),
        devices = setOf("device-1")
    )

    private fun createMockClient(
        responseBody: String,
        statusCode: HttpStatusCode = HttpStatusCode.OK,
        requestValidator: ((io.ktor.client.request.HttpRequestData) -> Unit)? = null
    ): HttpClient {
        val mockEngine = MockEngine { request ->
            requestValidator?.invoke(request)
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

    // ============ queryReader Tests ============

    @Test
    fun `queryReader returns Success when API returns 200`() = runTest {
        val mockClient = createMockClient(json.encodeToString(testReader))
        val readerApi = readerApiBuilder(mockClient, "http://test.com")

        readerApi.queryReader(testReaderId.toString()).test {
            val result = awaitItem()
            assertTrue(result is ApiResult.Success)
            assertEquals(testReader.name, result.data.name)
            assertEquals(testReader.emailRelay, result.data.emailRelay)
            awaitComplete()
        }
    }

    @Test
    fun `queryReader returns Error when API returns 404`() = runTest {
        val mockClient = createErrorMockClient(HttpStatusCode.NotFound, "Reader not found")
        val readerApi = readerApiBuilder(mockClient, "http://test.com")

        readerApi.queryReader("invalid-id").test {
            val result = awaitItem()
            assertTrue(result is ApiResult.Error)
            assertEquals(result.message?.contains("404"), true)
            awaitComplete()
        }
    }

    @Test
    fun `queryReader returns Error when API returns 500`() = runTest {
        val mockClient = createErrorMockClient(HttpStatusCode.InternalServerError, "Server error")
        val readerApi = readerApiBuilder(mockClient, "http://test.com")

        readerApi.queryReader(testReaderId.toString()).test {
            val result = awaitItem()
            assertTrue(result is ApiResult.Error)
            assertEquals(result.message?.contains("500"), true)
            awaitComplete()
        }
    }

    @Test
    fun `queryReader returns Error when exception is thrown`() = runTest {
        val mockClient = createExceptionMockClient(RuntimeException("Network error"))
        val readerApi = readerApiBuilder(mockClient, "http://test.com")

        readerApi.queryReader(testReaderId.toString()).test {
            val result = awaitItem()
            assertTrue(result is ApiResult.Error)
            assertEquals("Network error", result.message)
            awaitComplete()
        }
    }

    @Test
    fun `queryReader calls correct URL`() = runTest {
        var capturedUrl: String? = null
        val mockEngine = MockEngine { request ->
            capturedUrl = request.url.toString()
            respond(
                content = json.encodeToString(testReader),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val mockClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }

        val readerApi = readerApiBuilder(mockClient, "http://test.com")
        readerApi.queryReader("reader-123").test {
            awaitItem()
            awaitComplete()
        }

        assertEquals("http://test.com/readers/reader-123", capturedUrl)
    }

    @Test
    fun `queryReader uses GET method`() = runTest {
        var capturedMethod: HttpMethod? = null
        val mockEngine = MockEngine { request ->
            capturedMethod = request.method
            respond(
                content = json.encodeToString(testReader),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val mockClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }

        val readerApi = readerApiBuilder(mockClient, "http://test.com")
        readerApi.queryReader(testReaderId.toString()).test {
            awaitItem()
            awaitComplete()
        }

        assertEquals(HttpMethod.Get, capturedMethod)
    }

    @Test
    fun `queryReader returns reader with correct data`() = runTest {
        val mockClient = createMockClient(json.encodeToString(testReader))
        val readerApi = readerApiBuilder(mockClient, "http://test.com")

        readerApi.queryReader(testReaderId.toString()).test {
            val result = awaitItem() as ApiResult.Success
            assertEquals("Test Reader", result.data.name)
            assertEquals("test@example.com", result.data.emailRelay)
            assertEquals("12345", result.data.zipcode)
            assertEquals(4.5, result.data.avgRating)
            assertEquals(2, result.data.activeListings.size)
            awaitComplete()
        }
    }

    // ============ postReader Tests ============

    @Test
    fun `postReader returns Success when API returns 200`() = runTest {
        val returnedReader = testReader.copy(readerId = Uuid.random())
        val mockClient = createMockClient(json.encodeToString(returnedReader))
        val readerApi = readerApiBuilder(mockClient, "http://test.com")

        readerApi.postReader(testReader).test {
            val result = awaitItem()
            assertTrue(result is ApiResult.Success)
            assertNotNull(result.data.readerId)
            awaitComplete()
        }
    }

    @Test
    fun `postReader returns Success when API returns 201 Created`() = runTest {
        val returnedReader = testReader.copy(readerId = Uuid.random())
        val mockClient = createMockClient(
            json.encodeToString(returnedReader),
            HttpStatusCode.Created
        )
        val readerApi = readerApiBuilder(mockClient, "http://test.com")

        readerApi.postReader(testReader).test {
            val result = awaitItem()
            assertTrue(result is ApiResult.Success)
            awaitComplete()
        }
    }

    @Test
    fun `postReader returns Error when API returns 400`() = runTest {
        val mockClient = createErrorMockClient(HttpStatusCode.BadRequest, "Invalid reader data")
        val readerApi = readerApiBuilder(mockClient, "http://test.com")

        readerApi.postReader(testReader).test {
            val result = awaitItem()
            assertTrue(result is ApiResult.Error)
            assertEquals(result.message?.contains("400"), true)
            awaitComplete()
        }
    }

    @Test
    fun `postReader returns Error when API returns 409 Conflict`() = runTest {
        val mockClient = createErrorMockClient(HttpStatusCode.Conflict, "Reader already exists")
        val readerApi = readerApiBuilder(mockClient, "http://test.com")

        readerApi.postReader(testReader).test {
            val result = awaitItem()
            assertTrue(result is ApiResult.Error)
            assertEquals(result.message?.contains("409"), true)
            awaitComplete()
        }
    }

    @Test
    fun `postReader returns Error when exception is thrown`() = runTest {
        val mockClient = createExceptionMockClient(RuntimeException("Connection refused"))
        val readerApi = readerApiBuilder(mockClient, "http://test.com")

        readerApi.postReader(testReader).test {
            val result = awaitItem()
            assertTrue(result is ApiResult.Error)
            assertEquals("Connection refused", result.message)
            awaitComplete()
        }
    }

    @Test
    fun `postReader calls correct URL`() = runTest {
        var capturedUrl: String? = null
        val mockEngine = MockEngine { request ->
            capturedUrl = request.url.toString()
            respond(
                content = json.encodeToString(testReader),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val mockClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }

        val readerApi = readerApiBuilder(mockClient, "http://test.com")
        readerApi.postReader(testReader).test {
            awaitItem()
            awaitComplete()
        }

        assertEquals("http://test.com/readers", capturedUrl)
    }

    @Test
    fun `postReader uses POST method`() = runTest {
        var capturedMethod: HttpMethod? = null
        val mockEngine = MockEngine { request ->
            capturedMethod = request.method
            respond(
                content = json.encodeToString(testReader),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val mockClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }

        val readerApi = readerApiBuilder(mockClient, "http://test.com")
        readerApi.postReader(testReader).test {
            awaitItem()
            awaitComplete()
        }

        assertEquals(HttpMethod.Post, capturedMethod)
    }

    @Test
    fun `postReader handles reader with null pictureURL`() = runTest {
        val readerWithNullPicture = testReader.copy(pictureURL = null)
        val mockClient = createMockClient(json.encodeToString(readerWithNullPicture))
        val readerApi = readerApiBuilder(mockClient, "http://test.com")

        readerApi.postReader(readerWithNullPicture).test {
            val result = awaitItem()
            assertTrue(result is ApiResult.Success)
            assertNull(result.data.pictureURL)
            awaitComplete()
        }
    }

    @Test
    fun `postReader handles reader with empty collections`() = runTest {
        val readerWithEmptyCollections = testReader.copy(
            activeListings = emptySet(),
            followers = emptySet(),
            following = emptySet(),
            geofenceFiftyKms = emptySet(),
            devices = emptySet()
        )
        val mockClient = createMockClient(json.encodeToString(readerWithEmptyCollections))
        val readerApi = readerApiBuilder(mockClient, "http://test.com")

        readerApi.postReader(readerWithEmptyCollections).test {
            val result = awaitItem()
            assertTrue(result is ApiResult.Success)
            assertTrue(result.data.activeListings.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `postReader handles reader with null readerId`() = runTest {
        val readerWithNullId = testReader.copy(readerId = null)
        val returnedReader = readerWithNullId.copy(readerId = Uuid.random())
        val mockClient = createMockClient(json.encodeToString(returnedReader))
        val readerApi = readerApiBuilder(mockClient, "http://test.com")

        readerApi.postReader(readerWithNullId).test {
            val result = awaitItem()
            assertTrue(result is ApiResult.Success)
            assertNotNull(result.data.readerId)
            awaitComplete()
        }
    }

    // ============ readerApiBuilder Tests ============

    @Test
    fun `readerApiBuilder uses provided baseUrl`() = runTest {
        var capturedUrl: String? = null
        val mockEngine = MockEngine { request ->
            capturedUrl = request.url.toString()
            respond(
                content = json.encodeToString(testReader),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val mockClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }

        val readerApi = readerApiBuilder(mockClient, "http://custom-api.com")
        readerApi.queryReader("test-id").test {
            awaitItem()
            awaitComplete()
        }

        assertEquals(capturedUrl?.startsWith("http://custom-api.com"), true)
    }

    // ============ Error message handling Tests ============

    @Test
    fun `queryReader error includes server response in message`() = runTest {
        val serverErrorMessage = "Detailed server error message"
        val mockClient = createErrorMockClient(HttpStatusCode.BadRequest, serverErrorMessage)
        val readerApi = readerApiBuilder(mockClient, "http://test.com")

        readerApi.queryReader("invalid-id").test {
            val result = awaitItem()
            assertTrue(result is ApiResult.Error)
            assertEquals(result.message?.contains(serverErrorMessage), true)
            awaitComplete()
        }
    }

    @Test
    fun `postReader error includes server response in message`() = runTest {
        val serverErrorMessage = "Email already registered"
        val mockClient = createErrorMockClient(HttpStatusCode.Conflict, serverErrorMessage)
        val readerApi = readerApiBuilder(mockClient, "http://test.com")

        readerApi.postReader(testReader).test {
            val result = awaitItem()
            assertTrue(result is ApiResult.Error)
            assertEquals(result.message?.contains(serverErrorMessage), true)
            awaitComplete()
        }
    }
}
