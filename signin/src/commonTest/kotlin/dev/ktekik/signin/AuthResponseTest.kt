package dev.ktekik.signin

import dev.ktekik.signin.models.GoogleAccount
import dev.ktekik.signin.models.Profile
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AuthResponseTest {

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
    fun `Success contains correct GoogleAccount`() {
        val response = AuthResponse.Success(testGoogleAccount)
        assertEquals(testGoogleAccount, response.account)
    }

    @Test
    fun `Error contains correct message`() {
        val errorMessage = "Authentication failed"
        val response = AuthResponse.Error(errorMessage)
        assertEquals(errorMessage, response.message)
    }

    @Test
    fun `Cancelled is singleton object`() {
        val cancelled1 = AuthResponse.Cancelled
        val cancelled2 = AuthResponse.Cancelled
        assertTrue(cancelled1 === cancelled2)
    }

    @Test
    fun `doOnSuccess executes block for Success`() = runTest {
        var executedAccount: GoogleAccount? = null
        val response: AuthResponse = AuthResponse.Success(testGoogleAccount)

        response.doOnSuccess { account ->
            executedAccount = account
        }

        assertEquals(testGoogleAccount, executedAccount)
    }

    @Test
    fun `doOnSuccess does not execute block for Error`() = runTest {
        var blockExecuted = false
        val response: AuthResponse = AuthResponse.Error("Some error")

        response.doOnSuccess {
            blockExecuted = true
        }

        assertFalse(blockExecuted)
    }

    @Test
    fun `doOnSuccess does not execute block for Cancelled`() = runTest {
        var blockExecuted = false
        val response: AuthResponse = AuthResponse.Cancelled

        response.doOnSuccess {
            blockExecuted = true
        }

        assertFalse(blockExecuted)
    }

    @Test
    fun `Success account contains correct profile data`() {
        val response = AuthResponse.Success(testGoogleAccount)
        assertEquals("Test User", response.account.profile.name)
        assertEquals("test@example.com", response.account.profile.email)
        assertEquals("test-id-token", response.account.idToken)
        assertEquals("test-access-token", response.account.accessToken)
    }

    @Test
    fun `doOnSuccess receives correct account data`() = runTest {
        var receivedName: String? = null
        var receivedEmail: String? = null
        val response: AuthResponse = AuthResponse.Success(testGoogleAccount)

        response.doOnSuccess { account ->
            receivedName = account.profile.name
            receivedEmail = account.profile.email
        }

        assertEquals("Test User", receivedName)
        assertEquals("test@example.com", receivedEmail)
    }

    @Test
    fun `Error with empty message`() {
        val response = AuthResponse.Error("")
        assertEquals("", response.message)
    }

    @Test
    fun `Success with profile having null picture`() {
        val profileWithNullPicture = testProfile.copy(picture = null)
        val accountWithNullPicture = testGoogleAccount.copy(profile = profileWithNullPicture)
        val response = AuthResponse.Success(accountWithNullPicture)
        
        assertNull(response.account.profile.picture)
    }

    @Test
    fun `Success with profile having null zipcode`() {
        val profileWithNullZipcode = testProfile.copy(zipcode = null)
        val accountWithNullZipcode = testGoogleAccount.copy(profile = profileWithNullZipcode)
        val response = AuthResponse.Success(accountWithNullZipcode)
        
        assertNull(response.account.profile.zipcode)
    }
}
