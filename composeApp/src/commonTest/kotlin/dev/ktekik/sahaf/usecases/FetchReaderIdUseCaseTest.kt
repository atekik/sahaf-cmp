package dev.ktekik.sahaf.usecases

import app.cash.turbine.test
import dev.ktekik.sahaf.datastore.ReaderIdRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FetchReaderIdUseCaseTest {

    @Test
    fun `execute returns reader ID when available`() = runTest {
        val expectedReaderId = "reader-123"
        val fakeRepository = FetchReaderIdRepository(expectedReaderId)
        val useCase = FetchReaderIdUseCase(fakeRepository)

        useCase.execute(Unit).test {
            val result = awaitItem()
            assertEquals(expectedReaderId, result)
            awaitComplete()
        }
    }

    @Test
    fun `execute returns null when no reader ID stored`() = runTest {
        val fakeRepository = FetchReaderIdRepository(null)
        val useCase = FetchReaderIdUseCase(fakeRepository)

        useCase.execute(Unit).test {
            val result = awaitItem()
            assertNull(result)
            awaitComplete()
        }
    }

    @Test
    fun `execute returns empty string when reader ID is empty`() = runTest {
        val fakeRepository = FetchReaderIdRepository("")
        val useCase = FetchReaderIdUseCase(fakeRepository)

        useCase.execute(Unit).test {
            val result = awaitItem()
            assertEquals("", result)
            awaitComplete()
        }
    }

    @Test
    fun `execute returns correct UUID format reader ID`() = runTest {
        val uuidReaderId = "550e8400-e29b-41d4-a716-446655440000"
        val fakeRepository = FetchReaderIdRepository(uuidReaderId)
        val useCase = FetchReaderIdUseCase(fakeRepository)

        useCase.execute(Unit).test {
            val result = awaitItem()
            assertEquals(uuidReaderId, result)
            awaitComplete()
        }
    }

    @Test
    fun `execute ignores Unit parameter`() = runTest {
        val expectedReaderId = "reader-456"
        val fakeRepository = FetchReaderIdRepository(expectedReaderId)
        val useCase = FetchReaderIdUseCase(fakeRepository)

        // Calling with Unit should always work the same
        useCase.execute(Unit).test {
            assertEquals(expectedReaderId, awaitItem())
            awaitComplete()
        }
    }
}

// Test double
internal class FetchReaderIdRepository(
    fakeReaderId: String?
) : ReaderIdRepository(FetchReaderIdDataStore()) {
    override val readerId: Flow<String?> = flowOf(fakeReaderId)
    override suspend fun saveId(id: String) {}
}

// Minimal fake DataStore
private class FetchReaderIdDataStore : androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> {
    override val data: Flow<androidx.datastore.preferences.core.Preferences> = flowOf(
        androidx.datastore.preferences.core.emptyPreferences()
    )
    override suspend fun updateData(
        transform: suspend (t: androidx.datastore.preferences.core.Preferences) -> androidx.datastore.preferences.core.Preferences
    ): androidx.datastore.preferences.core.Preferences = androidx.datastore.preferences.core.emptyPreferences()
}
