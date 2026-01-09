package dev.ktekik.sahaf.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.ktekik.utils.composables.ErrorContainer
import org.koin.compose.koinInject

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = koinInject()) {
    val state: HomeScreenState by homeViewModel.container.stateFlow.collectAsStateWithLifecycle()

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
            title = "Something went wrong!",
            message = "Please try again later.",
            onTryAgainClicked = {
                homeViewModel.fetchReader()
            }
        )
    }
}
