package dev.ktekik.sahaf.fts

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.ktekik.sahaf.HideStatusBarComposable
import dev.ktekik.sahaf.navigation.FtsNavigationViewModel
import dev.ktekik.sahaf.navigation.NavigationSideEffect
import dev.ktekik.sahaf.theming.md_theme_light_shadow
import dev.ktekik.sahaf.utils.ResourcesImpl
import dev.ktekik.utils.LocalResources
import org.jetbrains.compose.resources.painterResource

@Composable
fun GetStartedScreen(viewModel: FtsNavigationViewModel, navController: NavController) {
    val navState = viewModel.container.stateFlow.collectAsState()

    HideStatusBarComposable()

    LaunchedEffect(navState) {
        viewModel.container.sideEffectFlow.collect { sideEffect ->
            when (sideEffect) {
                is NavigationSideEffect.NavigateTo -> {
                    navController.navigate(sideEffect.destination.route)
                }
            }
        }
    }

    CompositionLocalProvider(LocalResources provides ResourcesImpl()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets())
                .paint( // Use the .paint() modifier
                    painter = painterResource(LocalResources.current.drawables.background),
                    contentScale = ContentScale.Crop,
                    alpha = .8f
                )
        ) {

            LogoWithShadow(modifier = Modifier.align(Alignment.Center))

            GetStartedButton(Modifier.align(Alignment.BottomCenter), viewModel)
        }
    }
}

@Composable
fun GetStartedButton(modifier: Modifier, viewModel: FtsNavigationViewModel) {
    Button(
        modifier = modifier.padding(
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
        Text(
            text = "Get Started",
            color = LocalContentColor.current,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LogoWithShadow(modifier: Modifier) {
    Image(
        painter = painterResource(LocalResources.current.drawables.logo),
        contentDescription = "App Logo",
        modifier = modifier.dropShadow(
            shape = RectangleShape, shadow = Shadow(
                radius = 10.dp,
                spread = 6.dp,
                color = md_theme_light_shadow,
            )
        )
    )
}

//@OptIn(ExperimentalUuidApi::class)
//fun Profile.toReader(): Reader {
//    return Reader(
//        name = this.name,
//        emailRelay = this.email,
//        pictureURL = this.picture,
//        activeListings = emptySet(),
//        zipcode = "",
//        avgRating = 0.0,
//        followers = emptySet(),
//        following = emptySet(),
//        geofenceFiftyKms = emptySet(),
//        devices = emptySet(), // Fetch device id
//        readerId = null
//    )
//}
