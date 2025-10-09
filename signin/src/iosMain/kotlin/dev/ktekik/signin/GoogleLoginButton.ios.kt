package dev.ktekik.signin

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.ktekik.signin.models.GoogleAccount
import dev.ktekik.signin.models.Profile

//private val GIDSignInResult.toGoogleAccount: GoogleAccount
//    get() = GoogleAccount(
//        idToken = user.idToken?.tokenString.orEmpty(),
//        accessToken = user.accessToken.tokenString,
//        profile = Profile(
//            name = user.profile?.name.orEmpty(),
//            familyName = user.profile?.familyName.orEmpty(),
//            givenName = user.profile?.givenName.orEmpty(),
//            email = user.profile?.email.orEmpty(),
//            picture = user.profile?.imageURLWithDimension(100u)?.absoluteString
//        ),
//    )
//
//private val NSError.fullErrorMessage: String
//    get() {
//        val underlyingErrors = underlyingErrors.joinToString(", ") { it.toString() }
//        val recoveryOptions = localizedRecoveryOptions?.joinToString(", ") { it.toString() }
//
//        return listOfNotNull(
//            "code: $code",
//            domain?.let { "domain: $domain" },
//            "description: $localizedDescription",
//            localizedFailureReason?.let { "reason: $localizedFailureReason" },
//            localizedRecoverySuggestion?.let { "suggestion: $localizedRecoverySuggestion" },
//            "underlyingErrors: $underlyingErrors",
//            "recoveryOptions: $recoveryOptions".takeIf { recoveryOptions != null },
//        ).joinToString("\n")
//    }

@Composable
internal actual fun GoogleLoginButton(onResponse: (AuthResponse) -> Unit, modifier: Modifier) {
}