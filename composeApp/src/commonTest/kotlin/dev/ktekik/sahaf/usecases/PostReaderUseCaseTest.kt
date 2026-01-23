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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class PostReaderUseCaseTest {

    private val testReaderId = Uuid.random()
    private val testReader = Reader(
        readerId = testReaderId,
        name = "Test Reader",
        emailRelay = "test@example.com",
        pictureURL = "https://example.com/pic.jpg",
        activeListings = emptySet(),
        zipcode = "12345",
        avgRating = 0.0,
        followers = emptySet(),
        following = emptySet(),
        geofenceFiftyKms = emptySet(),
        devices = emptySet()
    )

    @Test
    fun `execute returns ReaderRegistryState with reader on success`() = runTest {
        val returnedReader = testReader.copy(readerId = Uuid.random())
        val postReaderApi = PostReaderApi(postResult = ApiResult.Success(returnedReader))
        val useCase = PostReaderUseCase(postReaderApi)

        useCase.execute(testReader).test {
            val result = awaitItem()
            assertNotNull(result.reader)
            assertEquals(returnedReader, result.reader)
            assertNull(result.error)
            awaitComplete()
        }
    }

    @Test
    fun `execute returns ReaderRegistryState with error on failure`() = runTest {
        val errorMessage = "Failed to register reader"
        val postReaderApi = PostReaderApi(postResult = ApiResult.Error(errorMessage))
        val useCase = PostReaderUseCase(postReaderApi)

        useCase.execute(testReader).test {
            val result = awaitItem()
            assertNull(result.reader)
            assertEquals(errorMessage, result.error)
            awaitComplete()
        }
    }

    @Test
    fun `execute returns ReaderRegistryState with null error message when API error is null`() = runTest {
        val postReaderApi = PostReaderApi(postResult = ApiResult.Error(null))
        val useCase = PostReaderUseCase(postReaderApi)

        useCase.execute(testReader).test {
            val result = awaitItem()
            assertNull(result.reader)
            assertNull(result.error)
            awaitComplete()
        }
    }

    @Test
    fun `execute passes correct reader to API`() = runTest {
        var capturedReader: Reader? = null
        val fakeReaderApi = object : ReaderApi {
            override suspend fun postReader(reader: Reader): Flow<ApiResult<Reader>> {
                capturedReader = reader
                return flowOf(ApiResult.Success(reader))
            }

            override suspend fun queryReader(readerId: String): Flow<ApiResult<Reader>> =
                flowOf(ApiResult.Error("Not implemented"))
        }

        val useCase = PostReaderUseCase(fakeReaderApi)
        useCase.execute(testReader).test {
            awaitItem()
            awaitComplete()
        }

        assertEquals(testReader, capturedReader)
    }

    @Test
    fun `execute handles reader with all fields populated`() = runTest {
        val fullReader = Reader(
            readerId = testReaderId,
            name = "Full Reader",
            emailRelay = "full@example.com",
            pictureURL = "https://example.com/full.jpg",
            activeListings = setOf("listing-1", "listing-2"),
            zipcode = "54321",
            avgRating = 4.5,
            followers = setOf("follower-1"),
            following = setOf("following-1"),
            geofenceFiftyKms = setOf("geo-1"),
            devices = setOf("device-1")
        )
        val postReaderApi = PostReaderApi(postResult = ApiResult.Success(fullReader))
        val useCase = PostReaderUseCase(postReaderApi)

        useCase.execute(fullReader).test {
            val result = awaitItem()
            assertNotNull(result.reader)
            assertEquals("Full Reader", result.reader.name)
            assertEquals("full@example.com", result.reader.emailRelay)
            assertEquals(4.5, result.reader.avgRating)
            awaitComplete()
        }
    }

    @Test
    fun `execute handles reader with null readerId`() = runTest {
        val readerWithNullId = testReader.copy(readerId = null)
        val postReaderApi = PostReaderApi(postResult = ApiResult.Success(readerWithNullId))
        val useCase = PostReaderUseCase(postReaderApi)

        useCase.execute(readerWithNullId).test {
            val result = awaitItem()
            assertNotNull(result.reader)
            assertNull(result.reader.readerId)
            awaitComplete()
        }
    }

    @Test
    fun `execute handles reader with null pictureURL`() = runTest {
        val readerWithNullPicture = testReader.copy(pictureURL = null)
        val returnedReader = readerWithNullPicture.copy(readerId = Uuid.random())
        val postReaderApi = PostReaderApi(postResult = ApiResult.Success(returnedReader))
        val useCase = PostReaderUseCase(postReaderApi)

        useCase.execute(readerWithNullPicture).test {
            val result = awaitItem()
            assertNotNull(result.reader)
            assertNull(result.reader.pictureURL)
            awaitComplete()
        }
    }

    @Test
    fun `execute handles reader with empty zipcode`() = runTest {
        val readerWithEmptyZipcode = testReader.copy(zipcode = "")
        val postReaderApi = PostReaderApi(postResult = ApiResult.Success(readerWithEmptyZipcode))
        val useCase = PostReaderUseCase(postReaderApi)

        useCase.execute(readerWithEmptyZipcode).test {
            val result = awaitItem()
            assertNotNull(result.reader)
            assertEquals("", result.reader.zipcode)
            awaitComplete()
        }
    }

    @Test
    fun `ReaderRegistryState isLoading defaults to false`() = runTest {
        val postReaderApi = PostReaderApi(postResult = ApiResult.Success(testReader))
        val useCase = PostReaderUseCase(postReaderApi)

        useCase.execute(testReader).test {
            val result = awaitItem()
            assertEquals(false, result.isLoading)
            awaitComplete()
        }
    }
}

// Test double
private class PostReaderApi(
    private val postResult: ApiResult<Reader>
) : ReaderApi {
    override suspend fun postReader(reader: Reader): Flow<ApiResult<Reader>> = flowOf(postResult)
    override suspend fun queryReader(readerId: String): Flow<ApiResult<Reader>> =
        flowOf(ApiResult.Error("Not implemented"))
}
