package dev.ktekik.sahaf.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.ktekik.utils.composables.ErrorContainer
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import sahaf.composeapp.generated.resources.Res
import sahaf.composeapp.generated.resources.error_something_went_wrong
import sahaf.composeapp.generated.resources.error_try_again_later

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = koinInject()) {
    val state: HomeScreenState by homeViewModel.container.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        homeViewModel.fetchReader()
    }

    when (val currentState = state) {
        is HomeScreenState.LoadingState -> HomeLoadingScreen()
        is HomeScreenState.ErrorState -> HomeErrorScreen()
        is HomeScreenState.ReadyState -> HomeReadyScreen(currentState)
    }
}

@Composable
private fun HomeLoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun HomeErrorScreen(homeViewModel: HomeViewModel = koinInject()) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        ErrorContainer(
            title = stringResource(Res.string.error_something_went_wrong),
            message = stringResource(Res.string.error_try_again_later),
            onTryAgainClicked = {
                homeViewModel.fetchReader()
            }
        )
    }
}
