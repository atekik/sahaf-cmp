package dev.ktekik.signin

import android.app.Activity
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dev.ktekik.signin.models.GoogleAccount
import dev.ktekik.signin.models.Profile

private val GoogleSignInAccount.googleAccount: GoogleAccount
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

private val ApiException.fullErrorMessage: String
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
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("565843012089-054i7pvbl8j7indc5nscqoeh5opjktes.apps.googleusercontent.com")
        .requestServerAuthCode("565843012089-054i7pvbl8j7indc5nscqoeh5opjktes.apps.googleusercontent.com")
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(LocalActivity.current!!, gso)

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val account = task.getResult(ApiException::class.java)

                onResponse(AuthResponse.Success(account.googleAccount))
            } catch (e: ApiException) {
                if (result.resultCode == Activity.RESULT_CANCELED) {
                    AuthResponse.Cancelled
                } else {
                    AuthResponse.Error(e.fullErrorMessage)
                }.also(onResponse)

                Log.w("Toto", "Google sign in failed", e)
            }

        }

    GoogleButtonUI(modifier = modifier, onClick = {
        launcher.launch(googleSignInClient.signInIntent)
    })
}
