package dev.ktekik.sahaf.navigation

import dev.ktekik.sahaf.datastore.ReaderIdRepository
import dev.ktekik.sahaf.usecases.FetchReaderIdUseCase
import dev.ktekik.signin.models.Profile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.orbitmvi.orbit.test.test
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class NavigationViewModelTest {

    private val testProfile = Profile(
        name = "Test User",
        familyName = "User",
        givenName = "Test",
        email = "test@example.com",
        picture = "https://example.com/pic.jpg",
        zipcode = "12345"
    )

    @AfterTest
    fun tearDown() {
    }

    @Test
    fun `initial state should have GetStarted as default destination`() = runTest {
        val viewModel = createViewModel(readerIdResult = null)

        viewModel.test(this) {
            val state = viewModel.container.stateFlow.value
            assertEquals(NavigationDestination.GetStarted, state.destination)
            assertNull(state.profile)
            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `navigateToGetStarted should emit NavigateTo GetStarted side effect`() = runTest {
        val viewModel = createViewModel(readerIdResult = null)

        viewModel.test(this) {
            containerHost.navigateToGetStarted()
            
            val sideEffect = awaitSideEffect()
            assertEquals(NavigationDestination.GetStarted, (sideEffect as NavigationSideEffect.NavigateTo).destination)
            
        }
    }

    @Test
    fun `navigateToGreeting should update state and emit side effect`() = runTest {
        val viewModel = createViewModel(readerIdResult = null)

        viewModel.test(this) {
            containerHost.navigateToGreeting()
            
            val state = awaitState()
            assertEquals(NavigationDestination.Welcome, state.destination)
            
            val sideEffect = awaitSideEffect()
            assertEquals(NavigationDestination.Welcome, (sideEffect as NavigationSideEffect.NavigateTo).destination)
            
        }
    }

    @Test
    fun `navigateToZipcodeEntry should update state with profile and emit side effect`() = runTest {
        val viewModel = createViewModel(readerIdResult = null)

        viewModel.test(this) {
            containerHost.navigateToZipcodeEntry(testProfile)
            
            val state = awaitState()
            assertEquals(NavigationDestination.ZipcodeEntry, state.destination)
            assertNotNull(state.profile)
            assertEquals(testProfile.email, state.profile.email)
            
            val sideEffect = awaitSideEffect() as NavigationSideEffect.NavigateTo
            assertEquals(NavigationDestination.ZipcodeEntry, sideEffect.destination)
            assertEquals(true, sideEffect.popUpTo)
            assertEquals(true, sideEffect.popUpToInclusive)
            
        }
    }

    @Test
    fun `registerProfile should update state and emit RegistrationPendingDialog side effect`() = runTest {
        val viewModel = createViewModel(readerIdResult = null)

        viewModel.test(this) {
            containerHost.registerProfile(testProfile)
            
            val state = awaitState()
            assertEquals(NavigationDestination.RegistrationPendingDialog, state.destination)
            assertEquals(testProfile, state.profile)
            
            val sideEffect = awaitSideEffect() as NavigationSideEffect.NavigateTo
            assertEquals(NavigationDestination.RegistrationPendingDialog, sideEffect.destination)
            assertEquals(true, sideEffect.popUpTo)
            assertEquals(true, sideEffect.popUpToInclusive)
            
        }
    }

    @Test
    fun `navigateHome should emit Home side effect with popUpTo`() = runTest {
        val viewModel = createViewModel(readerIdResult = null)

        viewModel.test(this) {
            containerHost.navigateHome()
            
            val sideEffect = awaitSideEffect() as NavigationSideEffect.NavigateTo
            assertEquals(NavigationDestination.Home, sideEffect.destination)
            assertEquals(true, sideEffect.popUpTo)
            assertEquals(true, sideEffect.popUpToInclusive)
            containerHost.container.cancel()
        }
    }

    @Test
    fun `navigateToRegistrationFailed should emit RegistrationFailedDialog side effect`() = runTest {
        val viewModel = createViewModel(readerIdResult = null)

        viewModel.test(this) {
            containerHost.navigateToRegistrationFailed()
            
            val sideEffect = awaitSideEffect() as NavigationSideEffect.NavigateTo
            assertEquals(NavigationDestination.RegistrationFailedDialog, sideEffect.destination)
            assertEquals(true, sideEffect.popUpTo)
            assertEquals(true, sideEffect.popUpToInclusive)
        }
    }

    @Test
    fun `onIsbnScanned should emit BookListing navigation side effect`() = runTest {
        val viewModel = createViewModel(readerIdResult = "reader-123")
        val testIsbn = "9780123456789"

        viewModel.test(this) {
            containerHost.onIsbnScanned(testIsbn)

            val sideEffect = awaitSideEffect() as NavigationSideEffect.NavigateTo
            assertEquals(NavigationDestination.PostFTS.BookListing, sideEffect.destination)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `profile should be stored in state after navigateToZipcodeEntry`() = runTest(
        UnconfinedTestDispatcher()
    ) {
        val viewModel = createViewModel(readerIdResult = null)

        viewModel.test(this) {
            containerHost.navigateToZipcodeEntry(testProfile)
            
            val state = awaitState()
            assertEquals("Test User", state.profile?.name)
            assertEquals("test@example.com", state.profile?.email)
            assertEquals("12345", state.profile?.zipcode)
            cancelAndIgnoreRemainingItems()
        }
    }

    private fun createViewModel(readerIdResult: String?): NavigationViewModel {
        val navigationFakeReaderIdRepository = NavigationFakeReaderIdRepository(readerIdResult)
        return NavigationViewModel(
            fetchReaderIdUseCase = FetchReaderIdUseCase(navigationFakeReaderIdRepository)
        )
    }
}

// Test double
private class NavigationFakeReaderIdRepository(fakeReaderId: String?) : ReaderIdRepository(
    NavigationFakeDateStore()
) {
    override val readerId: Flow<String?> = flowOf(fakeReaderId)
    override suspend fun saveId(id: String) {}
}

// Minimal fake DataStore
private class NavigationFakeDateStore : androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> {
    override val data: Flow<androidx.datastore.preferences.core.Preferences> = flowOf(
        androidx.datastore.preferences.core.emptyPreferences()
    )
    override suspend fun updateData(
        transform: suspend (t: androidx.datastore.preferences.core.Preferences) -> androidx.datastore.preferences.core.Preferences
    ): androidx.datastore.preferences.core.Preferences = androidx.datastore.preferences.core.emptyPreferences()
}
