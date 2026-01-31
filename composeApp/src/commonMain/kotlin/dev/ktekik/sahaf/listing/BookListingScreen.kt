package dev.ktekik.sahaf.listing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.ktekik.utils.composables.ErrorContainer
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import sahaf.composeapp.generated.resources.Res
import sahaf.composeapp.generated.resources.failed_to_load_book

@Composable
fun BookListingScreen(
    isbn: String,
    onBackPressed: () -> Unit,
    isbnQueryViewModel: IsbnQueryViewModel = koinInject(),
    createBookListingViewModel: CreateBookListingViewModel = koinInject(),
) {
    val state by isbnQueryViewModel.container.stateFlow.collectAsStateWithLifecycle()
    val listingState by createBookListingViewModel.container.stateFlow.collectAsStateWithLifecycle()


    LaunchedEffect(isbn) {
        isbnQueryViewModel.fetchBook(isbn)
    }

    when (val currentState = state) {
        is BookListingScreenState.Loading -> BookListingLoadingScreen()
        is BookListingScreenState.Error -> BookListingErrorScreen(
            message = currentState.message,
            onRetry = { isbnQueryViewModel.fetchBook(isbn) }
        )

        is BookListingScreenState.Ready -> BookListingReadyScreen(
            book = currentState.book,
            onContinuePressed = { book, map ->
                createBookListingViewModel.createBookListing(book, map)
            },
            onBackPressed = onBackPressed,
        )
    }

    when (val currentState = listingState) {
        is CreateBookListingScreenState.Idle -> Unit
        CreateBookListingScreenState.Loading -> BookListingLoadingScreen()
        is CreateBookListingScreenState.Success -> createBookListingViewModel.navigateHome()
        is CreateBookListingScreenState.Error -> BookListingErrorScreen(
            message = currentState.message,
            onRetry = { createBookListingViewModel.postBookListing() },
            onBackPressed = {
                createBookListingViewModel.onBackPressed()
            }
        )
    }
}

@Composable
private fun BookListingLoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun BookListingErrorScreen(
    message: String,
    onRetry: () -> Unit,
    onBackPressed: () -> Unit = {}
) {
    val backHandlerState = rememberNavigationEventState(
        currentInfo = NavigationEventInfo.None
    )
    NavigationBackHandler(
        state = backHandlerState,
        onBackCompleted = onBackPressed
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ErrorContainer(
            title = stringResource(Res.string.failed_to_load_book),
            message = message,
            onTryAgainClicked = onRetry
        )
    }
}
