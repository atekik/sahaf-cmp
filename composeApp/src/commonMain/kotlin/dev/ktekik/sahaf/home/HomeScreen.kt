package dev.ktekik.sahaf.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.koinInject

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = koinInject()) {
    val state: HomeScreenState by homeViewModel.container.stateFlow.collectAsStateWithLifecycle()

    when(val currentState = state) {
        is HomeScreenState.LoadingState -> HomeLoadingScreen()
        is HomeScreenState.ErrorState -> HomeErrorScreen(currentState)
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
private fun HomeErrorScreen(currentState: HomeScreenState.ErrorState) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Error: ${currentState.error}",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun HomeReadyScreen(currentState: HomeScreenState.ReadyState) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
        ) {
            Text(
                "Name: ${currentState.reader.name}",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Email: ${currentState.reader.emailRelay}",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Picture: ${currentState.reader.pictureURL}",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Zipcode: ${currentState.reader.zipcode}",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}
