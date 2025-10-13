package dev.ktekik.signin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import dev.ktekik.signin.di.SIGN_IN_REQUEST_CODE
import dev.ktekik.signin.di.SignInResult
import dev.ktekik.signin.models.GoogleAccount
import dev.ktekik.signin.models.Profile
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.orbitmvi.orbit.compose.collectAsState

val GoogleSignInAccount.googleAccount: GoogleAccount
    get() = GoogleAccount(
        idToken = idToken.orEmpty(),
        accessToken = serverAuthCode.orEmpty(),
        profile = Profile(
            name = displayName.orEmpty(),
            familyName = familyName.orEmpty(),
            givenName = givenName.orEmpty(),
            email = email.orEmpty(),
            picture = photoUrl?.toString().orEmpty()
        ),
    )

val ApiException.fullErrorMessage: String
    get() {
        return listOfNotNull(
            "code: $statusCode",
            message?.let { "message: $message" },
            "localizedMessage: $localizedMessage",
            "status: $status"
        ).joinToString("\n")
    }

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
