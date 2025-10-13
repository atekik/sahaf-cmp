package dev.ktekik.sahaf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.ktekik.sahaf.utils.ResourcesImpl
import dev.ktekik.signin.AuthResponse
import dev.ktekik.signin.GoogleButton
import dev.ktekik.utils.LocalResources
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize().background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CompositionLocalProvider(LocalResources provides ResourcesImpl()) {
                MaterialTheme {
                    var snackbarMessage by remember { mutableStateOf("") }

                    Column(
                        modifier = Modifier.weight(1f).background(Color.White),
                        verticalArrangement = Arrangement.Center,
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

                        Spacer(modifier = Modifier.height(20.dp))

                        if (userName.isNotEmpty()) {
                            Text("Welcome $userName")
                        } else if (snackbarMessage.isNotEmpty()) {
                            Text(
                                text = snackbarMessage,
                                color = MaterialTheme.colorScheme.error // Use your theme's error color
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SignInSnackbar(message: String) {
    val snackbarHostState = remember { SnackbarHostState() }

    if (message.isNotEmpty()) {
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = "Dismiss",
                duration = SnackbarDuration.Indefinite
            )
        }
    }

    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { Snackbar(it) },
    )
}