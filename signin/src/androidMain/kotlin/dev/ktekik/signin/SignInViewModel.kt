package dev.ktekik.signin

import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)

class SignInViewModel : ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: GoogleSignInResult) {
//        _state.update { it.copy(isSignInSuccessful = result.data != null, signInError = result.errorMessage) }
    }

    fun resetState() {
        _state.update { SignInState() }
    }
}