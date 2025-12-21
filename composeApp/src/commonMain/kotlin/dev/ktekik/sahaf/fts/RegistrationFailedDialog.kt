package dev.ktekik.sahaf.fts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.ktekik.sahaf.navigation.FtsNavigationViewModel
import dev.ktekik.sahaf.reader.ReaderRegistryViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import sahaf.composeapp.generated.resources.Res
import sahaf.composeapp.generated.resources.error_icon

@Composable
fun RegistrationFailedDialog(
    readerRegistryViewModel: ReaderRegistryViewModel,
) {
    val viewModel: FtsNavigationViewModel = koinInject()
    val navState by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val registrationState by readerRegistryViewModel.container.stateFlow.collectAsStateWithLifecycle()

    Dialog(onDismissRequest = { }) {
        Column(
            modifier = Modifier
                .height(420.dp)
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(16.dp)
                ).padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ErrorContainer(
                title = "Reader Registration Failed",
                message = "Something went wrong. Please make sure you are connected to internet and try again.",
                showTryAgainText = !registrationState.isLoading
            ) {
                readerRegistryViewModel.registerReader(navState.profile, {
                    viewModel.navigateHome()
                })
            }
        }
    }
}

@Composable
fun ErrorContainer(
    title: String,
    message: String,
    showTryAgainText: Boolean = true,
    onTryAgainClicked: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.error_icon),
            contentDescription = "Error Icon",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onTryAgainClicked,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            if (showTryAgainText) {
                Text(text = "Try Again")
            } else {
                CircularProgressIndicator()
            }
        }
    }
}
