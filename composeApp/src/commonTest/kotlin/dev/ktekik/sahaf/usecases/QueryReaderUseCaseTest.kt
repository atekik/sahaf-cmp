package dev.ktekik.sahaf.usecases

import app.cash.turbine.test
import dev.ktekik.sahaf.cloud.ApiResult
import dev.ktekik.sahaf.cloud.ReaderApi
import dev.ktekik.sahaf.models.Reader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class QueryReaderUseCaseTest {

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

    @Test
    fun `execute returns Success when API returns reader`() = runTest {
        val queryReaderApi = QueryReaderApi(queryResult = ApiResult.Success(testReader))
        val useCase = QueryReaderUseCase(queryReaderApi)

        useCase.execute(testReaderId.toString()).test {
            val result = awaitItem()
            assertTrue(result is ReaderResponse.Success)
            assertEquals(testReader, result.reader)
            awaitComplete()
        }
    }

    @Test
    fun `execute returns Error when API returns error`() = runTest {
        val errorMessage = "Reader not found"
        val queryReaderApi = QueryReaderApi(queryResult = ApiResult.Error(errorMessage))
        val useCase = QueryReaderUseCase(queryReaderApi)

        useCase.execute("invalid-reader-id").test {
            val result = awaitItem()
            assertTrue(result is ReaderResponse.Error)
            assertEquals(errorMessage, result.message)
            awaitComplete()
        }
    }

    @Test
    fun `execute returns Error with null message when API error message is null`() = runTest {
        val queryReaderApi = QueryReaderApi(queryResult = ApiResult.Error(null))
        val useCase = QueryReaderUseCase(queryReaderApi)

        useCase.execute("invalid-reader-id").test {
            val result = awaitItem()
            assertTrue(result is ReaderResponse.Error)
            assertNull(result.message)
            awaitComplete()
        }
    }

    @Test
    fun `execute passes correct reader ID to API`() = runTest {
        var capturedReaderId: String? = null
        val fakeReaderApi = object : ReaderApi {
            override suspend fun queryReader(readerId: String): Flow<ApiResult<Reader>> {
                capturedReaderId = readerId
                return flowOf(ApiResult.Success(testReader))
            }

            override suspend fun postReader(reader: Reader): Flow<ApiResult<Reader>> =
                flowOf(ApiResult.Error("Not implemented"))
        }

        val useCase = QueryReaderUseCase(fakeReaderApi)
        useCase.execute("reader-123").test {
            awaitItem()
            awaitComplete()
        }

        assertEquals("reader-123", capturedReaderId)
    }

    @Test
    fun `Success result contains correct reader details`() = runTest {
        val queryReaderApi = QueryReaderApi(queryResult = ApiResult.Success(testReader))
        val useCase = QueryReaderUseCase(queryReaderApi)

        useCase.execute(testReaderId.toString()).test {
            val result = awaitItem() as ReaderResponse.Success
            assertEquals("Test Reader", result.reader.name)
            assertEquals("test@example.com", result.reader.emailRelay)
            assertEquals("12345", result.reader.zipcode)
            assertEquals(4.5, result.reader.avgRating)
            assertEquals(2, result.reader.activeListings.size)
            awaitComplete()
        }
    }

    @Test
    fun `execute handles reader with empty collections`() = runTest {
        val readerWithEmptyCollections = testReader.copy(
            activeListings = emptySet(),
            followers = emptySet(),
            following = emptySet(),
            geofenceFiftyKms = emptySet(),
            devices = emptySet()
        )
        val queryReaderApi = QueryReaderApi(queryResult = ApiResult.Success(readerWithEmptyCollections))
        val useCase = QueryReaderUseCase(queryReaderApi)

        useCase.execute(testReaderId.toString()).test {
            val result = awaitItem() as ReaderResponse.Success
            assertTrue(result.reader.activeListings.isEmpty())
            assertTrue(result.reader.followers.isEmpty())
            assertTrue(result.reader.following.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `execute handles reader with null pictureURL`() = runTest {
        val readerWithNullPicture = testReader.copy(pictureURL = null)
        val queryReaderApi = QueryReaderApi(queryResult = ApiResult.Success(readerWithNullPicture))
        val useCase = QueryReaderUseCase(queryReaderApi)

        useCase.execute(testReaderId.toString()).test {
            val result = awaitItem() as ReaderResponse.Success
            assertNull(result.reader.pictureURL)
            awaitComplete()
        }
    }
}

// Test double
private class QueryReaderApi(
    private val queryResult: ApiResult<Reader>
) : ReaderApi {
    override suspend fun queryReader(readerId: String): Flow<ApiResult<Reader>> = flowOf(queryResult)
    override suspend fun postReader(reader: Reader): Flow<ApiResult<Reader>> =
        flowOf(ApiResult.Error("Not implemented"))
}
