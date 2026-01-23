package dev.ktekik.signin

import dev.ktekik.signin.di.SignInResult
import dev.ktekik.signin.models.GoogleAccount
import dev.ktekik.signin.models.Profile
import kotlinx.coroutines.test.runTest
import org.orbitmvi.orbit.test.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SignInViewModelTest {

    private val testProfile = Profile(
        name = "Test User",
        familyName = "User",
        givenName = "Test",
        email = "test@example.com",
        picture = "https://example.com/pic.jpg",
        zipcode = "12345"
    )

    private val testGoogleAccount = GoogleAccount(
        idToken = "test-id-token",
        accessToken = "test-access-token",
        profile = testProfile
    )

    @Test
    fun `initial state should have no account and no progress bar`() = runTest {
        val viewModel = SignInViewModel()

        viewModel.test(this) {
            val state = awaitState()
            assertFalse(state.progressBar)
            assertNull(state.account)
            assertFalse(state.isCancelled)
            assertNull(state.signInError)
        }
    }

    @Test
    fun `onSignInClick should show progress bar`() = runTest {
        val viewModel = SignInViewModel()

        viewModel.test(this) {
            expectInitialState()
            
            containerHost.onSignInClick()
            
            val state = awaitState()
            assertTrue(state.progressBar)
        }
    }

    @Test
    fun `onSignInResult Success should update state with account`() = runTest {
        val viewModel = SignInViewModel()

        viewModel.test(this) {
            expectInitialState()
            
            containerHost.onSignInResult(SignInResult.Success(testGoogleAccount))
            
            val state = awaitState()
            assertEquals(testGoogleAccount, state.account)
            assertFalse(state.progressBar)
            assertFalse(state.isCancelled)
            assertNull(state.signInError)
        }
    }

    @Test
    fun `onSignInResult Error should update state with error message`() = runTest {
        val viewModel = SignInViewModel()
        val errorMessage = "Sign in failed"

        viewModel.test(this) {
            expectInitialState()
            
            containerHost.onSignInResult(SignInResult.Error(errorMessage))
            
            val state = awaitState()
            assertNull(state.account)
            assertFalse(state.progressBar)
            assertFalse(state.isCancelled)
            assertEquals(errorMessage, state.signInError)
        }
    }

    @Test
    fun `onSignInResult Cancelled should update state with isCancelled true`() = runTest {
        val viewModel = SignInViewModel()

        viewModel.test(this) {
            expectInitialState()
            
            containerHost.onSignInResult(SignInResult.Cancelled)
            
            val state = awaitState()
            assertNull(state.account)
            assertFalse(state.progressBar)
            assertTrue(state.isCancelled)
            assertNull(state.signInError)
        }
    }

    @Test
    fun `successful sign in should contain correct profile data`() = runTest {
        val viewModel = SignInViewModel()

        viewModel.test(this) {
            expectInitialState()
            
            containerHost.onSignInResult(SignInResult.Success(testGoogleAccount))
            
            val state = awaitState()
            assertEquals("Test User", state.account?.profile?.name)
            assertEquals("test@example.com", state.account?.profile?.email)
            assertEquals("test-id-token", state.account?.idToken)
            assertEquals("test-access-token", state.account?.accessToken)
        }
    }

    @Test
    fun `progress bar should be shown when sign in starts then hidden after result`() = runTest {
        val viewModel = SignInViewModel()

        viewModel.test(this) {
            expectInitialState()
            
            // Start sign in
            containerHost.onSignInClick()
            var state = awaitState()
            assertTrue(state.progressBar)
            
            // Complete sign in
            containerHost.onSignInResult(SignInResult.Success(testGoogleAccount))
            state = awaitState()
            assertFalse(state.progressBar)
        }
    }

    @Test
    fun `error message should be set correctly for various error types`() = runTest {
        val viewModel = SignInViewModel()

        viewModel.test(this) {
            expectInitialState()
            
            containerHost.onSignInResult(SignInResult.Error("Network error"))
            var state = awaitState()
            assertEquals("Network error", state.signInError)
        }
    }

    @Test
    fun `sign in can be retried after cancellation`() = runTest {
        val viewModel = SignInViewModel()

        viewModel.test(this) {
            expectInitialState()
            
            // First attempt - cancelled
            containerHost.onSignInResult(SignInResult.Cancelled)
            var state = awaitState()
            assertTrue(state.isCancelled)
            
            // Second attempt - click sign in again
            containerHost.onSignInClick()
            state = awaitState()
            assertTrue(state.progressBar)
            
            // Second attempt - success
            containerHost.onSignInResult(SignInResult.Success(testGoogleAccount))
            state = awaitState()
            assertFalse(state.isCancelled)
            assertEquals(testGoogleAccount, state.account)
        }
    }

    @Test
    fun `sign in can be retried after error`() = runTest {
        val viewModel = SignInViewModel()

        viewModel.test(this) {
            expectInitialState()
            
            // First attempt - error
            containerHost.onSignInResult(SignInResult.Error("Failed"))
            var state = awaitState()
            assertEquals("Failed", state.signInError)
            
            // Second attempt - click sign in again
            containerHost.onSignInClick()
            state = awaitState()
            assertTrue(state.progressBar)
            
            // Second attempt - success
            containerHost.onSignInResult(SignInResult.Success(testGoogleAccount))
            state = awaitState()
            assertNull(state.signInError)
            assertEquals(testGoogleAccount, state.account)
        }
    }
}
