package dev.ktekik.signin

import androidx.lifecycle.ViewModel
import dev.ktekik.signin.di.SignInResult
import dev.ktekik.signin.models.GoogleAccount
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

data class SignInState(
    val progressBar: Boolean = false,
    val account: GoogleAccount? = null,
    val isCancelled: Boolean = false,
    val signInError: String? = null
)

class SignInViewModel : ViewModel(), ContainerHost<SignInState, Unit> {

    fun onSignInClick() {
        intent {
            reduce { SignInState(progressBar = true) }
        }
    }

    // todo look into hoisting this state up instead of using callbacks
    fun onSignInResult(result: SignInResult) {
        intent {
            when (result) {
                is SignInResult.Success -> reduce { SignInState(account = result.account) }
                is SignInResult.Error -> reduce { SignInState(signInError = result.message) }
                is SignInResult.Cancelled -> reduce { SignInState(isCancelled = true) }
            }
        }
    }

    override val container: Container<SignInState, Unit> = container(SignInState())
}