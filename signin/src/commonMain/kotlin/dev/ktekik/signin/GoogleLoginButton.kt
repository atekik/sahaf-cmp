package dev.ktekik.signin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import dev.ktekik.signin.usecase.FirebaseAuthUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
internal expect fun GoogleLoginButton(
    onResponse: (AuthResponse) -> Unit,
    modifier: Modifier = Modifier
)

@Composable
fun GoogleButton(
    onResponse: (AuthResponse) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    GoogleLoginButton(
        modifier = modifier,
        onResponse = { response ->
            coroutineScope.launch(Dispatchers.IO) {
                FirebaseAuthUseCase(response).invoke()

                withContext(Dispatchers.Main) { onResponse(response) }
            }
        }
    )
}