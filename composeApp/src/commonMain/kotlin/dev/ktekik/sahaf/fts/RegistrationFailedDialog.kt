package dev.ktekik.sahaf.fts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.ktekik.sahaf.navigation.NavigationViewModel
import dev.ktekik.sahaf.reader.ReaderRegistryViewModel
import dev.ktekik.utils.composables.ErrorContainer
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import sahaf.composeapp.generated.resources.Res
import sahaf.composeapp.generated.resources.registration_failed_message
import sahaf.composeapp.generated.resources.registration_failed_title

@Composable
fun RegistrationFailedDialog(
    readerRegistryViewModel: ReaderRegistryViewModel,
) {
    val viewModel: NavigationViewModel = koinInject()
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
                title = stringResource(Res.string.registration_failed_title),
                message = stringResource(Res.string.registration_failed_message),
                showTryAgainText = !registrationState.isLoading
            ) {
                readerRegistryViewModel.registerReader(navState.profile, {
                    viewModel.navigateHome()
                })
            }
        }
    }
}
