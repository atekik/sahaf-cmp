package dev.ktekik.sahaf.fts

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.ktekik.sahaf.navigation.NavigationViewModel
import dev.ktekik.sahaf.reader.ReaderRegistryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import sahaf.composeapp.generated.resources.Res
import sahaf.composeapp.generated.resources.loading_vector
import sahaf.composeapp.generated.resources.reader_registering_icon

// Colors matching the screenshot description

@Composable
fun RegistrationPendingDialog(
    readerRegistryViewModel: ReaderRegistryViewModel,
    onDismissRequest: () -> Unit = {},
) {
    val viewModel: NavigationViewModel = koinInject()
    val navState by viewModel.container.stateFlow.collectAsStateWithLifecycle()

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Top section with user icon and decorative bubbles
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {

                    // Large dark brown circle with person icon
                    Box(
                        modifier = Modifier
                            .size(165.dp)
                            .clip(RectangleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.reader_registering_icon),
                            contentDescription = "Person Icon",
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Primary message
                Text(
                    text = "Sign In Successful!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Informational message
                Text(
                    text = "Your account is being created. Please wait a moment, we are preparing for you.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                VectorCircularProgressIndicator()
            }
        }
    }

    LaunchedEffect(Unit) {

        launch(Dispatchers.IO) {
            navState.profile?.let {
                readerRegistryViewModel.registerReader(it, {
                    viewModel.navigateHome()
                }) {
                    viewModel.navigateToRegistrationFailed()
                }
            } ?: throw IllegalStateException("Profile is null, not expected")
        }
    }
}

@Composable
fun VectorCircularProgressIndicator(
//    imageVector: ImageVector, // Or use Painter for drawable resources
    modifier: Modifier = Modifier,
    durationMillis: Int = 1000
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spinner")

    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Image(
        painter = painterResource(Res.drawable.loading_vector),
        contentDescription = "Loading",
        modifier = modifier
            .rotate(angle) // Apply the rotation here
    )
}

