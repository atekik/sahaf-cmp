package dev.ktekik.signin.di

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dev.ktekik.sahaf.BuildConfig
import dev.ktekik.signin.fullErrorMessage
import dev.ktekik.signin.googleAccount
import dev.ktekik.signin.models.GoogleAccount
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    factory<ActivityResultContract<Int, SignInResult>> { AuthResultContract() }
}

private class AuthResultContract : ActivityResultContract<Int, SignInResult>() {
    override fun createIntent(context: Context, input: Int): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
            .requestServerAuthCode(BuildConfig.GOOGLE_CLIENT_ID)
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        return googleSignInClient.signInIntent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): SignInResult {
        return if (resultCode == Activity.RESULT_OK && intent != null) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            try {
                val account = task.getResult(ApiException::class.java)
                SignInResult.Success(account.googleAccount)
            } catch (e: ApiException) {
                SignInResult.Error(e.fullErrorMessage)
            }
        } else {
            SignInResult.Cancelled
        }
    }
}

sealed interface SignInResult {
    data class Success(val account: GoogleAccount) : SignInResult
    data class Error(val message: String) : SignInResult
    data object Cancelled : SignInResult
}

const val SIGN_IN_REQUEST_CODE = 1

