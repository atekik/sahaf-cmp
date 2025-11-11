package dev.ktekik.sahaf

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import dev.ktekik.sahaf.cloud.Reader
import dev.ktekik.sahaf.navigation.NavigationViewModel
import dev.ktekik.sahaf.utils.ResourcesImpl
import dev.ktekik.signin.models.Profile
import dev.ktekik.utils.LocalResources
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun WelcomeScreen(viewModel: NavigationViewModel) {
    CompositionLocalProvider(LocalResources provides ResourcesImpl()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .paint( // Use the .paint() modifier
                    painter = painterResource(LocalResources.current.drawables.background),
                    contentScale = ContentScale.Crop,
                    alpha = .8f
                )
        ) {

            Image(
                painter = painterResource(LocalResources.current.drawables.logo),
                contentDescription = "App Logo",
                modifier = Modifier.align(Alignment.Center)
            )
            Button(
                modifier = Modifier.align(Alignment.BottomCenter).padding(
                    horizontal = 32.dp,
                    vertical = 128.dp
                ).fillMaxWidth(),
                onClick = { viewModel.navigateToGreeting() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 12.dp,
                    pressedElevation = 16.dp,
                    focusedElevation = 10.dp
                ),
                shape = MaterialTheme.shapes.extraLarge,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(text = "Get Started",
                    color = LocalContentColor.current,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}

@Composable
@Preview
fun App() {
    // Preview version without ViewModel
    CompositionLocalProvider(LocalResources provides ResourcesImpl()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .paint(
                    painter = painterResource(LocalResources.current.drawables.background),
                    contentScale = ContentScale.Crop,
                    alpha = .8f
                )
        ) {
            Image(
                painter = painterResource(LocalResources.current.drawables.logo),
                contentDescription = "App Logo",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
fun Profile.toReader(): Reader {
    return Reader(
        name = this.name,
        emailRelay = this.email,
        pictureURL = this.picture,
        activeListings = emptySet(),
        zipcode = "",
        avgRating = 0.0,
        followers = emptySet(),
        following = emptySet(),
        geofenceFiftyKms = emptySet(),
        devices = emptySet(), // Fetch device id
        readerId = null
    )
}
