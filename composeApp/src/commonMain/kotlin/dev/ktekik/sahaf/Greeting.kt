package dev.ktekik.sahaf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.ktekik.sahaf.utils.ResourcesImpl
import dev.ktekik.signin.AuthResponse
import dev.ktekik.signin.GoogleButton
import dev.ktekik.utils.LocalResources

@Composable
fun Greeting() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CompositionLocalProvider(LocalResources provides ResourcesImpl()) {
            var snackbarMessage by remember { mutableStateOf("") }

            Column(
                modifier = Modifier.weight(1f).padding(64.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var userName: String by remember { mutableStateOf("") }

                GoogleButton(
                    onResponse = {
                        (it as? AuthResponse.Success)?.account?.profile?.name?.let { name ->
                            userName = name
                            snackbarMessage = ""
                        }

                        (it as? AuthResponse.Error)?.message?.let { message ->
                            userName = ""
                            snackbarMessage = message
                        }

                        (it as? AuthResponse.Cancelled)?.let {
                            userName = ""
                            snackbarMessage = "Error: Cancelled"
                        }
                    }
                )
            }
        }
    }
}