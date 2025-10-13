package dev.ktekik.signin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.ktekik.signin.di.SIGN_IN_REQUEST_CODE
import dev.ktekik.signin.di.SignInResult
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.orbitmvi.orbit.compose.collectAsState

@Composable
internal actual fun GoogleLoginButton(
    onResponse: (AuthResponse) -> Unit,
    modifier: Modifier
) {

    val signInViewModel: SignInViewModel = koinViewModel()
    val authResultContract = koinInject<ActivityResultContract<Int, SignInResult>>()

    val state = signInViewModel.collectAsState()

    val launcher =
        rememberLauncherForActivityResult(contract = authResultContract) { result ->
            signInViewModel.onSignInResult(result)
        }

    state.value.account?.let {
        onResponse(AuthResponse.Success(it))
    } ?: state.value.signInError?.let {
        onResponse(AuthResponse.Error(it))
    } ?: state.value.isCancelled.let {
        onResponse(AuthResponse.Cancelled)
    }

    GoogleButtonUI(
        modifier = modifier,
        onClick = {
            launcher.launch(SIGN_IN_REQUEST_CODE)
            signInViewModel.onSignInClick()
        },
        showProgressBar = state.value.progressBar
    )
}

@Preview
@Composable
private fun GoogleLoginButtonPreview() {
    GoogleLoginButton(onResponse = {})
}
