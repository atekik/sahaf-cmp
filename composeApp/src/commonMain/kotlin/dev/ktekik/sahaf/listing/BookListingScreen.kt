package dev.ktekik.sahaf.listing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun BookListingScreen(
    isbn: String,
    onBackPressed: () -> Unit,
    isbnQueryViewModel: IsbnQueryViewModel = koinInject(),
    bookListingViewModel: BookListingViewModel = koinInject(),
) {
    val state by isbnQueryViewModel.container.stateFlow.collectAsStateWithLifecycle()
    val listingState by bookListingViewModel.container.stateFlow.collectAsStateWithLifecycle()
    val lifecycleCoroutineScope = rememberCoroutineScope()

    LaunchedEffect(isbn) {
        isbnQueryViewModel.fetchBook(isbn)
    }

    when (val currentState = state) {
        is BookListingScreenState.Loading -> BookListingLoadingScreen()
        is BookListingScreenState.Error -> BookListingErrorScreen(
            message = currentState.message,
            onRetry = { isbnQueryViewModel.fetchBook(isbn) }
        )

        is BookListingScreenState.Ready -> BookListingCreationScreen(
            book = currentState.book,
            onContinuePressed = { book, map ->
                bookListingViewModel.createBookListing(book, map, lifecycleCoroutineScope)
            },
            onBackPressed = onBackPressed,
        )
    }

    when (val currentState = listingState) {
        is CreateBookListingScreenState.Idle -> Unit
        CreateBookListingScreenState.Loading -> BookListingLoadingScreen()
        is CreateBookListingScreenState.Success -> bookListingViewModel.navigateHome()
        is CreateBookListingScreenState.Error -> BookListingErrorScreen(
            message = currentState.message,
            onRetry = { bookListingViewModel.postBookListing(lifecycleCoroutineScope) },
            onBackPressed = {
                bookListingViewModel.onBackPressed()
            }
        )
    }
}

@OptIn(ExperimentalUuidApi::class)
@Composable
internal fun ListingDetailScreen(
    listingId: String,
    onBackPressed: () -> Unit,
    listingDetailViewModel: ListingDetailViewModel = koinInject(),
    bookListingViewModel: BookListingViewModel = koinInject(),
) {
    val state by listingDetailViewModel.container.stateFlow.collectAsStateWithLifecycle()
    val listingState by bookListingViewModel.container.stateFlow.collectAsStateWithLifecycle()
    val lifecycleCoroutineScope = rememberCoroutineScope()

    LaunchedEffect(listingId) {
        listingDetailViewModel.fetchListing(listingId)
    }

    when (val currentState = state) {
        is ListingDetailScreenState.Loading -> BookListingLoadingScreen()
        is ListingDetailScreenState.Error -> BookListingErrorScreen(
            message = currentState.message,
            onRetry = { listingDetailViewModel.fetchListing(listingId) }
        )

        is ListingDetailScreenState.Ready -> {
            if (currentState.isSelf) {
                BookListingUpdateScreen(
                    bookListing = currentState.listing,
                    onSubmitPressed = { book, map ->
                        // update book listing with new delivery method
                        // TODO add an option to delete the book listing and a dedicated callback
                        bookListingViewModel.updateBookListing(
                            book.copy(
                                deliveryMethod = bookListingViewModel.getDeliveryMethod(
                                    map
                                )
                            ),
                            scope = lifecycleCoroutineScope
                        )
                    },
                    onBackPressed = onBackPressed,
                )
            } else {
                // Create BookListingReadOnlyScreen where the only option is to contact the owner of the book
            }
        }

    }

    when (val currentState = listingState) {
        is CreateBookListingScreenState.Idle -> Unit
        CreateBookListingScreenState.Loading -> BookListingLoadingScreen()
        is CreateBookListingScreenState.Success -> bookListingViewModel.navigateHome()
        is CreateBookListingScreenState.Error -> BookListingErrorScreen(
            message = currentState.message,
            onRetry = { bookListingViewModel.postBookListing(lifecycleCoroutineScope) },
            onBackPressed = {
                bookListingViewModel.onBackPressed()
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
