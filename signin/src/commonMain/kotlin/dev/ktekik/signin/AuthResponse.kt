package dev.ktekik.signin

import dev.ktekik.signin.models.GoogleAccount

sealed interface AuthResponse {
    data class Success(val account: GoogleAccount): AuthResponse
    data class Error(val message: String): AuthResponse
    data object Cancelled: AuthResponse

    suspend fun doOnSuccess(block: suspend (GoogleAccount) -> Unit) {
        when(this) {
            is Success -> block(account)
            Cancelled -> Unit
            is Error -> Unit
        }
    }
}