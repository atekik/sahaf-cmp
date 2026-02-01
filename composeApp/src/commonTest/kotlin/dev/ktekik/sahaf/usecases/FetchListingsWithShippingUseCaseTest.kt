package dev.ktekik.sahaf.usecases

import app.cash.turbine.test
import dev.ktekik.sahaf.cloud.ApiResult
import dev.ktekik.sahaf.cloud.BookApi
import dev.ktekik.sahaf.cloud.ReaderApi
import dev.ktekik.sahaf.home.HomeScreenState
import dev.ktekik.sahaf.models.Book
import dev.ktekik.sahaf.models.BookListing
import dev.ktekik.sahaf.models.CursorPagedListings
import dev.ktekik.sahaf.models.Reader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class FetchListingsWithShippingUseCaseTest {

    private val testReaderId = Uuid.random()
    private val testReader = Reader(
        readerId = testReaderId,
        name = "Test Reader",
        emailRelay = "test@example.com",
        pictureURL = "https://example.com/pic.jpg",
        activeListings = emptySet(),
        zipcode = "12345",
        avgRating = 4.5,
        followers = emptySet(),
        following = emptySet(),
        geofenceFiftyKms = emptySet(),
        devices = emptySet()
    )

    private val testListings = CursorPagedListings(
        items = emptyList(),
        nextCursor = null
    )

    @Test
    fun `execute returns ReadyState when both API calls succeed`() = runTest {
        val fetchListingsWithShippingApi = FetchListingsWithShippingApi(listingsResult = ApiResult.Success(testListings))
        val fakeQueryReaderApi = FakeQueryReaderApi(queryResult = ApiResult.Success(testReader))
        val useCase = FetchListingsWithShippingUseCase(fetchListingsWithShippingApi, fakeQueryReaderApi)

        val params = FetchListingsParams(
            readerId = testReaderId.toString(),
            excludeSelf = false
        )

        useCase.execute(params).test {
            val result = awaitItem()
            assertTrue(result is HomeScreenState.ReadyState)
            assertEquals(testReader, result.reader)
            assertEquals(testListings, result.listings)
            awaitComplete()
        }
    }

    @Test
    fun `execute returns ErrorState when reader API fails`() = runTest {
        val errorMessage = "Failed to fetch reader"
        val fetchListingsWithShippingApi = FetchListingsWithShippingApi(listingsResult = ApiResult.Success(testListings))
        val fakeQueryReaderApi = FakeQueryReaderApi(queryResult = ApiResult.Error(errorMessage))
        val useCase = FetchListingsWithShippingUseCase(fetchListingsWithShippingApi, fakeQueryReaderApi)

        val params = FetchListingsParams(
            readerId = testReaderId.toString(),
            excludeSelf = false
        )

        useCase.execute(params).test {
            val result = awaitItem()
            assertTrue(result is HomeScreenState.ErrorState)
            assertEquals(errorMessage, result.error)
            awaitComplete()
        }
    }

    @Test
    fun `execute returns ErrorState when listings API fails`() = runTest {
        val errorMessage = "Failed to fetch listings"
        val fetchListingsWithShippingApi = FetchListingsWithShippingApi(listingsResult = ApiResult.Error(errorMessage))
        val fakeQueryReaderApi = FakeQueryReaderApi(queryResult = ApiResult.Success(testReader))
        val useCase = FetchListingsWithShippingUseCase(fetchListingsWithShippingApi, fakeQueryReaderApi)

        val params = FetchListingsParams(
            readerId = testReaderId.toString(),
            excludeSelf = false
        )

        useCase.execute(params).test {
            val result = awaitItem()
            assertTrue(result is HomeScreenState.ErrorState)
            assertEquals(errorMessage, result.error)
            awaitComplete()
        }
    }

    @Test
    fun `execute returns ErrorState with Generic Error when reader error message is null`() = runTest {
        val fetchListingsWithShippingApi = FetchListingsWithShippingApi(listingsResult = ApiResult.Success(testListings))
        val fakeQueryReaderApi = FakeQueryReaderApi(queryResult = ApiResult.Error(null))
        val useCase = FetchListingsWithShippingUseCase(fetchListingsWithShippingApi, fakeQueryReaderApi)

        val params = FetchListingsParams(
            readerId = testReaderId.toString(),
            excludeSelf = false
        )

        useCase.execute(params).test {
            val result = awaitItem()
            assertTrue(result is HomeScreenState.ErrorState)
            assertEquals("Generic Error", result.error)
            awaitComplete()
        }
    }

    @Test
    fun `execute returns ErrorState with Generic Error when listings error message is null`() = runTest {
        val fetchListingsWithShippingApi = FetchListingsWithShippingApi(listingsResult = ApiResult.Error(null))
        val fakeQueryReaderApi = FakeQueryReaderApi(queryResult = ApiResult.Success(testReader))
        val useCase = FetchListingsWithShippingUseCase(fetchListingsWithShippingApi, fakeQueryReaderApi)

        val params = FetchListingsParams(
            readerId = testReaderId.toString(),
            excludeSelf = false
        )

        useCase.execute(params).test {
            val result = awaitItem()
            assertTrue(result is HomeScreenState.ErrorState)
            assertEquals("Generic Error", result.error)
            awaitComplete()
        }
    }

    @Test
    fun `execute passes correct parameters to BookApi`() = runTest {
        var capturedReaderId: String? = null
        var capturedExcludeSelf: Boolean? = null
        var capturedLimit: Int? = null
        var capturedBefore: Instant? = null

        val fakeBookApi = object : BookApi {
            override suspend fun getListingsWithShipping(
                readerId: String,
                excludeSelf: Boolean,
                limit: Int?,
                before: Instant?
            ): Flow<ApiResult<CursorPagedListings>> {
                capturedReaderId = readerId
                capturedExcludeSelf = excludeSelf
                capturedLimit = limit
                capturedBefore = before
                return flowOf(ApiResult.Success(testListings))
            }

            override suspend fun queryByIsbn(isbn: String): Flow<ApiResult<Book>> =
                flowOf(ApiResult.Error("Not implemented"))

            override suspend fun createListing(bookListing: BookListing): Flow<ApiResult<BookListing>> {
                TODO("Not yet implemented")
            }
        }

        val fakeQueryReaderApi = FakeQueryReaderApi(queryResult = ApiResult.Success(testReader))
        val useCase = FetchListingsWithShippingUseCase(fakeBookApi, fakeQueryReaderApi)

        val testBefore = Instant.parse("2024-01-01T00:00:00Z")
        val params = FetchListingsParams(
            readerId = "reader-123",
            excludeSelf = true,
            limit = 10,
            before = testBefore
        )

        useCase.execute(params).test {
            awaitItem()
            awaitComplete()
        }

        assertEquals("reader-123", capturedReaderId)
        assertEquals(true, capturedExcludeSelf)
        assertEquals(10, capturedLimit)
        assertEquals(testBefore, capturedBefore)
    }

    @Test
    fun `execute passes correct reader ID to ReaderApi`() = runTest {
        var capturedReaderId: String? = null

        val fetchListingsWithShippingApi = FetchListingsWithShippingApi(listingsResult = ApiResult.Success(testListings))
        val fakeReaderApi = object : ReaderApi {
            override suspend fun queryReader(readerId: String): Flow<ApiResult<Reader>> {
                capturedReaderId = readerId
                return flowOf(ApiResult.Success(testReader))
            }

            override suspend fun postReader(reader: Reader): Flow<ApiResult<Reader>> =
                flowOf(ApiResult.Error("Not implemented"))
        }

        val useCase = FetchListingsWithShippingUseCase(fetchListingsWithShippingApi, fakeReaderApi)
        val params = FetchListingsParams(
            readerId = "reader-456",
            excludeSelf = false
        )

        useCase.execute(params).test {
            awaitItem()
            awaitComplete()
        }

        assertEquals("reader-456", capturedReaderId)
    }

    @Test
    fun `execute with default excludeSelf parameter`() = runTest {
        var capturedExcludeSelf: Boolean? = null

        val fakeBookApi = object : BookApi {
            override suspend fun getListingsWithShipping(
                readerId: String,
                excludeSelf: Boolean,
                limit: Int?,
                before: Instant?
            ): Flow<ApiResult<CursorPagedListings>> {
                capturedExcludeSelf = excludeSelf
                return flowOf(ApiResult.Success(testListings))
            }

            override suspend fun queryByIsbn(isbn: String): Flow<ApiResult<Book>> =
                flowOf(ApiResult.Error("Not implemented"))

            override suspend fun createListing(bookListing: BookListing): Flow<ApiResult<BookListing>> {
                TODO("Not yet implemented")
            }
        }

        val fakeQueryReaderApi = FakeQueryReaderApi(queryResult = ApiResult.Success(testReader))
        val useCase = FetchListingsWithShippingUseCase(fakeBookApi, fakeQueryReaderApi)

        // Using default excludeSelf = true
        val params = FetchListingsParams(readerId = "reader-123")

        useCase.execute(params).test {
            awaitItem()
            awaitComplete()
        }

        assertEquals(true, capturedExcludeSelf)
    }

    @Test
    fun `ReadyState contains correct reader and listings data`() = runTest {
        val fetchListingsWithShippingApi = FetchListingsWithShippingApi(listingsResult = ApiResult.Success(testListings))
        val fakeQueryReaderApi = FakeQueryReaderApi(queryResult = ApiResult.Success(testReader))
        val useCase = FetchListingsWithShippingUseCase(fetchListingsWithShippingApi, fakeQueryReaderApi)

        val params = FetchListingsParams(
            readerId = testReaderId.toString(),
            excludeSelf = false
        )

        useCase.execute(params).test {
            val result = awaitItem() as HomeScreenState.ReadyState
            assertEquals("Test Reader", result.reader.name)
            assertEquals("12345", result.reader.zipcode)
            assertTrue(result.listings.items.isEmpty())
            assertEquals(null, result.listings.nextCursor)
            awaitComplete()
        }
    }
}

// Test doubles
private class FetchListingsWithShippingApi(
    private val listingsResult: ApiResult<CursorPagedListings>
) : BookApi {
    override suspend fun getListingsWithShipping(
        readerId: String,
        excludeSelf: Boolean,
        limit: Int?,
        before: Instant?
    ): Flow<ApiResult<CursorPagedListings>> = flowOf(listingsResult)

    override suspend fun queryByIsbn(isbn: String): Flow<ApiResult<Book>> =
        flowOf(ApiResult.Error("Not implemented"))

    override suspend fun createListing(bookListing: BookListing): Flow<ApiResult<BookListing>> {
        TODO("Not yet implemented")
    }
}

private class FakeQueryReaderApi(
    private val queryResult: ApiResult<Reader>
) : ReaderApi {
    override suspend fun queryReader(readerId: String): Flow<ApiResult<Reader>> = flowOf(queryResult)
    override suspend fun postReader(reader: Reader): Flow<ApiResult<Reader>> =
        flowOf(ApiResult.Error("Not implemented"))
}
