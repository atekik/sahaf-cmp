package dev.ktekik.sahaf.listing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ktekik.sahaf.models.BookListing
import dev.ktekik.sahaf.navigation.NavigationSideEffect
import dev.ktekik.sahaf.usecases.FetchReaderIdZipcodePairUseCase
import dev.ktekik.sahaf.usecases.QueryBookListingByIdResult
import dev.ktekik.sahaf.usecases.QueryBookListingByIdUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import kotlin.uuid.ExperimentalUuidApi

sealed interface ListingDetailScreenState {
    data object Loading : ListingDetailScreenState
    data class Ready(val listing: BookListing, val isSelf: Boolean = false) :
        ListingDetailScreenState

    data class Error(val message: String) : ListingDetailScreenState
}

class ListingDetailViewModel(
    private val queryBookListingByIdUseCase: QueryBookListingByIdUseCase,
    private val readerIdZipcodePairUseCase: FetchReaderIdZipcodePairUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel(), ContainerHost<ListingDetailScreenState, NavigationSideEffect> {

    override val container: Container<ListingDetailScreenState, NavigationSideEffect> =
        container(ListingDetailScreenState.Loading)

    @OptIn(ExperimentalUuidApi::class)
    fun fetchListing(listingId: String) {
        viewModelScope.launch(dispatcher) {
            val pair = readerIdZipcodePairUseCase.execute(Unit)
                .filterNotNull()
                .first()


            queryBookListingByIdUseCase.execute(listingId).collect { result ->
                intent {
                    reduce {
                        when (result) {
                            is QueryBookListingByIdResult.Success -> ListingDetailScreenState.Ready(
                                result.listing,
                                result.listing.readerId.toString() == pair.id
                            )

                            is QueryBookListingByIdResult.Error -> ListingDetailScreenState.Error(
                                result.message
                            )
                        }
                    }
                }
            }
        }
    }
}
