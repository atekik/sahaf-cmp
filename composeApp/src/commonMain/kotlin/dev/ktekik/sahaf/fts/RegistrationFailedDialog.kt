package dev.ktekik.sahaf.fts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.ktekik.sahaf.navigation.FtsNavigationViewModel
import dev.ktekik.sahaf.reader.ReaderRegistryViewModel
import dev.ktekik.sahaf.reader.toReader
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import sahaf.composeapp.generated.resources.Res
import sahaf.composeapp.generated.resources.error_icon

@Composable
fun RegistrationFailedDialog(
    onDismissRequest: () -> Unit = {},
    readerRegistryViewModel: ReaderRegistryViewModel,
) {
    val viewModel: FtsNavigationViewModel = koinInject()
    val navState by viewModel.container.stateFlow.collectAsState()
    val registrationState by readerRegistryViewModel.container.stateFlow.collectAsState()

    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
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
                text = "User Registration Failed!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Something went wrong. Please make sure you are connected to internet and try again.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    navState.profile?.let {
                        readerRegistryViewModel.registerReader(it.toReader(), {
                            // navigate home
                        }) { 
                            // no-op
                        }
                    } ?: IllegalStateException("Profile is null")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                if (registrationState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text(text = "Try Again")
                }
            }
        }
    }
}
