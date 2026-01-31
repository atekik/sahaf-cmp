package dev.ktekik.sahaf.reader

import dev.ktekik.sahaf.cloud.ApiResult
import dev.ktekik.sahaf.cloud.ReaderApi
import dev.ktekik.sahaf.datastore.ReaderIdZipcodePair
import dev.ktekik.sahaf.datastore.ReaderRepository
import dev.ktekik.sahaf.models.Reader
import dev.ktekik.sahaf.usecases.PostReaderUseCase
import dev.ktekik.sahaf.usecases.SaveReaderIdUseCase
import dev.ktekik.signin.models.Profile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.orbitmvi.orbit.test.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalCoroutinesApi::class)
class ReaderRegistryViewModelTest {

    private val testReaderId = Uuid.random()
    private val testProfile = Profile(
        name = "Test User",
        familyName = "User",
        givenName = "Test",
        email = "test@example.com",
        picture = "https://example.com/pic.jpg",
        zipcode = "12345"
    )
    private val testReader = Reader(
        readerId = testReaderId,
        name = "Test User",
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
    fun `initial state should have no reader and not loading`() = runTest {
        val viewModel = createViewModel(
            postResult = ApiResult.Success(testReader),
            saveCalled = {}
        )

        viewModel.test(this) {
            val state = viewModel.container.stateFlow.value
            assertNull(state.reader)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }
    }

    @Test
    fun `registerReader should set isLoading to true initially`() = runTest {
        val viewModel = createViewModel(
            postResult = ApiResult.Success(testReader),
            saveCalled = {}
        )

        viewModel.test(this) {
            containerHost.registerReader(testProfile, onSuccess = {}, onError = {})
            
            val state = awaitState()
            assertTrue(state.isLoading)
            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `registerReader should update state with reader on success`() = runTest {
        val viewModel = createViewModel(
            postResult = ApiResult.Success(testReader),
            saveCalled = {}
        )

        viewModel.test(this) {
            containerHost.registerReader(testProfile, onSuccess = {}, onError = {})
            
            skipItems(1) // Skip loading state
            
            val state = awaitState()
            assertFalse(state.isLoading)
            assertNotNull(state.reader)
            assertEquals(testReader, state.reader)
            assertNull(state.error)
        }
    }

    @Test
    fun `registerReader should call onSuccess callback on success`() = runTest {
        var successCalled = false
        var receivedReader: Reader? = null

        val viewModel = createViewModel(
            postResult = ApiResult.Success(testReader),
            saveCalled = {}
        )

        viewModel.test(this) {
            containerHost.registerReader(
                testProfile,
                onSuccess = { reader ->
                    successCalled = true
                    receivedReader = reader
                },
                onError = {}
            )
            
            awaitState()

            awaitState()
        }

        assertTrue(successCalled)
        assertEquals(testReader, receivedReader)
    }

    @Test
    fun `registerReader should save reader ID on success`() = runTest {
        var savedPair: ReaderIdZipcodePair? = null

        val viewModel = createViewModel(
            postResult = ApiResult.Success(testReader),
            saveCalled = { savedPair = it }
        )

        viewModel.test(this) {
            containerHost.registerReader(testProfile, onSuccess = {}, onError = {})
            
            // Wait for loading state
            awaitState() // isLoading = true
            // Wait for completion state  
            awaitState() // isLoading = false, reader populated
        }

        assertEquals(testReaderId.toString(), savedPair?.id)
    }

    @Test
    fun `registerReader should update state with error on failure`() = runTest {
        val errorMessage = "Registration failed"
        val viewModel = createViewModel(
            postResult = ApiResult.Error(errorMessage),
            saveCalled = {}
        )

        viewModel.test(this) {
            containerHost.registerReader(testProfile, onSuccess = {}, onError = {})
            
            skipItems(1) // Skip loading state
            
            val state = awaitState()
            assertFalse(state.isLoading)
            assertNull(state.reader)
            assertEquals(errorMessage, state.error)
        }
    }

    @Test
    fun `registerReader should call onError callback on failure`() = runTest {
        var errorCalled = false

        val viewModel = createViewModel(
            postResult = ApiResult.Error("Registration failed"),
            saveCalled = {}
        )

        viewModel.test(this) {
            containerHost.registerReader(
                testProfile,
                onSuccess = {},
                onError = { errorCalled = true }
            )
            
            awaitState()

            awaitState()
        }

        assertTrue(errorCalled)
    }

    @Test
    fun `registerReader with null profile should throw exception`() = runTest {
        val viewModel = createViewModel(
            postResult = ApiResult.Success(testReader),
            saveCalled = {}
        )

        viewModel.test(this) {
            try {
                containerHost.registerReader(null, onSuccess = {}, onError = {})
                cancelAndIgnoreRemainingItems()
            } catch (e: IllegalStateException) {
                assertEquals("Profile is null", e.message)
            }
        }
    }

    @Test
    fun `registerReader should not save ID when reader has null readerId`() = runTest {
        val readerWithNullId = testReader.copy(readerId = null)
        var saveCalled = false

        val viewModel = createViewModel(
            postResult = ApiResult.Success(readerWithNullId),
            saveCalled = { saveCalled = true }
        )

        viewModel.test(this) {
            containerHost.registerReader(testProfile, onSuccess = {}, onError = {})
            
            cancelAndIgnoreRemainingItems()
        }

        assertFalse(saveCalled)
    }

    @Test
    fun `state should reflect correct reader data`() = runTest {
        val viewModel = createViewModel(
            postResult = ApiResult.Success(testReader),
            saveCalled = {}
        )

        viewModel.test(this) {
            containerHost.registerReader(testProfile, onSuccess = {}, onError = {})
            
            skipItems(1) // Skip loading state
            
            val state = awaitState()
            assertEquals("Test User", state.reader?.name)
            assertEquals("test@example.com", state.reader?.emailRelay)
            assertEquals("12345", state.reader?.zipcode)
            assertEquals(0.0, state.reader?.avgRating)
        }
    }

    private fun createViewModel(
        postResult: ApiResult<Reader>,
        saveCalled: (ReaderIdZipcodePair) -> Unit
    ): ReaderRegistryViewModel {
        val fakeReaderApi = FakeReaderApi(postResult)
        val fakeReaderIdRepository = FakeReaderRepository(saveCalled)

        return ReaderRegistryViewModel(
            postReaderUseCase = PostReaderUseCase(fakeReaderApi),
            saveReaderIdUseCase = SaveReaderIdUseCase(fakeReaderIdRepository),
            mainDispatcher = UnconfinedTestDispatcher()
        )
    }
}

// Test doubles
private class FakeReaderApi(
    private val postResult: ApiResult<Reader>
) : ReaderApi {
    override suspend fun postReader(reader: Reader): Flow<ApiResult<Reader>> = flowOf(postResult)
    override suspend fun queryReader(readerId: String): Flow<ApiResult<Reader>> = 
        flowOf(ApiResult.Error("Not implemented"))
}

private class FakeReaderRepository(
    private val onSave: (ReaderIdZipcodePair) -> Unit
) : ReaderRepository(FakeDataStore()) {
    override val readerId: Flow<String?> = flowOf(null)
    override suspend fun savePair(pair: ReaderIdZipcodePair) {
        onSave(pair)
    }
}

// Minimal fake DataStore
private class FakeDataStore : androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> {
    override val data: Flow<androidx.datastore.preferences.core.Preferences> = flowOf(
        androidx.datastore.preferences.core.emptyPreferences()
    )
    override suspend fun updateData(
        transform: suspend (t: androidx.datastore.preferences.core.Preferences) -> androidx.datastore.preferences.core.Preferences
    ): androidx.datastore.preferences.core.Preferences = androidx.datastore.preferences.core.emptyPreferences()
}
