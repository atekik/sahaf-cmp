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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.ktekik.sahaf.HideStatusBarComposable
import dev.ktekik.sahaf.navigation.NavigationViewModel
import dev.ktekik.sahaf.theming.md_theme_light_shadow
import dev.ktekik.utils.LocalResources
import dev.ktekik.utils.ResourcesImpl
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable fun SplashScreen() {
    CompositionLocalProvider(LocalResources provides ResourcesImpl()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets())
                .paint(
                    painter = painterResource(LocalResources.current.drawables.background),
                    contentScale = ContentScale.Crop,
                    alpha = .8f
                )
        ) {

            LogoWithShadow(modifier = Modifier.align(Alignment.Center))
        }
    }
}
@Composable
fun GetStartedScreen() {

    HideStatusBarComposable()

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

            GetStartedButton(Modifier.align(Alignment.BottomCenter))
        }
    }
}

@Composable
fun GetStartedButton(modifier: Modifier) {
    val viewModel: NavigationViewModel = koinInject()

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
