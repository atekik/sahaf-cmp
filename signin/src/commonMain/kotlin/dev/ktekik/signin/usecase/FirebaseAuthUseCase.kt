package dev.ktekik.signin.usecase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth
import dev.ktekik.signin.AuthResponse

internal class FirebaseAuthUseCase(val authResponse: AuthResponse) : UseCase {
    override suspend fun invoke() {
        authResponse.doOnSuccess { account ->
            Firebase.auth.signInWithCredential(
                GoogleAuthProvider.credential(account.idToken, account.accessToken)
            )
        }
    }
}
