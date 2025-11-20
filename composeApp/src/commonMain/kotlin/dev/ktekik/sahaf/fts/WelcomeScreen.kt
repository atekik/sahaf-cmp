package dev.ktekik.sahaf.fts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.ktekik.sahaf.ShowStatusBarComposable
import dev.ktekik.sahaf.navigation.FtsNavigationViewModel
import dev.ktekik.sahaf.utils.ResourcesImpl
import dev.ktekik.signin.AuthResponse
import dev.ktekik.signin.GoogleButton
import dev.ktekik.utils.LocalResources
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun WelcomeScreen() {
    val viewModel: FtsNavigationViewModel = koinInject()

    ShowStatusBarComposable()

    Column(
        modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CompositionLocalProvider(LocalResources provides ResourcesImpl()) {
            var snackbarMessage by remember { mutableStateOf("") }

            Column(
                modifier = Modifier.weight(1f).padding(64.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LogoWithShadow(Modifier.padding(top = 32.dp))

                Text(
                    text = "Welcome to Sahaf",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(32.dp)
                )

                Text(
                    text = "A platform to share Turkish books",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )


                val bookDrawable = if (isSystemInDarkTheme()) {
                    LocalResources.current.drawables.bookDark
                } else {
                    LocalResources.current.drawables.bookLight
                }

                Image(
                    painter = painterResource(bookDrawable),
                    contentDescription = "Book image",
                    modifier = Modifier.padding(vertical = 32.dp)
                )

                Text(
                    text = "Reserve. Meet. Borrow. Read.\n" +
                            "List. Ship. Rinse & Repeat.",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
            }


            GoogleButton(
                modifier = Modifier.padding(bottom = 64.dp),
                onResponse = {
                    (it as? AuthResponse.Success)?.account?.profile?.let { profile ->
                        viewModel.navigateToZipcodeEntry(profile)
                        snackbarMessage = ""
                    }

                    (it as? AuthResponse.Error)?.message?.let { message ->
                        snackbarMessage = "Error: Authentication Failed"
                    }

                    (it as? AuthResponse.Cancelled)?.let {
                        snackbarMessage = "Error: Cancelled"
                    }
                }
            )

            if (snackbarMessage.isNotEmpty()) {
                Snackbar(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                    action = {
                        TextButton(onClick = { snackbarMessage = "" }) {
                            Text("Dismiss", color = MaterialTheme.colorScheme.onError)
                        }
                    }
                ) {
                    Text(text = snackbarMessage)
                }
            }
        }
    }
}
