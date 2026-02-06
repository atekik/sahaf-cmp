package dev.ktekik.sahaf.listing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.koinInject
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
internal fun ListingDetailScreen(
    listingId: String,
    onBackPressed: () -> Unit,
    listingDetailViewModel: ListingDetailViewModel = koinInject(),
    bookListingViewModel: BookListingViewModel = koinInject(),
    deleteBookListingViewModel: DeleteBookListingViewModel = koinInject(),
) {
    val state by listingDetailViewModel.container.stateFlow.collectAsStateWithLifecycle()
    val listingState by bookListingViewModel.container.stateFlow.collectAsStateWithLifecycle()
    val deleteState by deleteBookListingViewModel.container.stateFlow.collectAsStateWithLifecycle()
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
                    onDeletePressed = {
                        deleteBookListingViewModel.deleteListing(listingId)
                    }
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

    when (val currentDeleteState = deleteState) {
        is DeleteBookListingScreenState.Idle -> Unit
        DeleteBookListingScreenState.Loading -> BookListingLoadingScreen()
        is DeleteBookListingScreenState.Success -> bookListingViewModel.navigateHome()
        is DeleteBookListingScreenState.Error -> BookListingErrorScreen(
            message = currentDeleteState.message,
            onRetry = { deleteBookListingViewModel.deleteListing(listingId) },
        )
    }
}