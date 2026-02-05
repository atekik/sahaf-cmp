package dev.ktekik.sahaf.listing

import androidx.lifecycle.ViewModel
import dev.ktekik.sahaf.models.Book
import dev.ktekik.sahaf.models.BookListing
import dev.ktekik.sahaf.models.DeliveryMethod
import dev.ktekik.sahaf.navigation.NavigationDestination
import dev.ktekik.sahaf.navigation.NavigationSideEffect
import dev.ktekik.sahaf.usecases.CreateBookListingResult
import dev.ktekik.sahaf.usecases.CreateBookListingUseCase
import dev.ktekik.sahaf.usecases.FetchReaderIdZipcodePairUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

sealed interface CreateBookListingScreenState {
    data object Idle : CreateBookListingScreenState
    data object Loading : CreateBookListingScreenState
    data class Success(val listing: BookListing) : CreateBookListingScreenState
    data class Error(val message: String) : CreateBookListingScreenState
}

@OptIn(ExperimentalUuidApi::class)
class BookListingViewModel(
    private val createBookListingUseCase: CreateBookListingUseCase,
    private val fetchReaderIdZipcodePairUseCase: FetchReaderIdZipcodePairUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel(), ContainerHost<CreateBookListingScreenState, NavigationSideEffect> {

    override val container: Container<CreateBookListingScreenState, NavigationSideEffect> =
        container(CreateBookListingScreenState.Idle)

    private var bookListing: BookListing? = null


    fun createBookListing(book: Book, deliveryMethodMap: Map<DeliveryMethod, Boolean>, scope: CoroutineScope) {
        intent {
            reduce { CreateBookListingScreenState.Loading }
            val readerIdZipcodePair = fetchReaderIdZipcodePairUseCase.execute(Unit)
                .filterNotNull()
                .first()

            bookListing = BookListing(
                book = book,
                deliveryMethod = getDeliveryMethod(deliveryMethodMap),
                zipcode = readerIdZipcodePair.zipcode,
                readerId = Uuid.parse(readerIdZipcodePair.id),
            )

            postBookListing(scope)
        }
    }

    fun updateBookListing(updatedListing: BookListing, scope: CoroutineScope) {
        intent {
            reduce { CreateBookListingScreenState.Loading }

            bookListing = updatedListing
            postBookListing(scope)
        }
    }

    internal fun postBookListing(scope: CoroutineScope) {
        scope.launch(dispatcher) {
            bookListing?.let {
                createBookListingUseCase.execute(it).collect { result ->
                    intent {
                        reduce {
                            when (result) {
                                is CreateBookListingResult.Success -> {
                                    bookListing = null
                                    CreateBookListingScreenState.Success(result.listing)
                                }

                                is CreateBookListingResult.Error -> {
                                    CreateBookListingScreenState.Error(result.message)
                                }
                            }
                        }
                    }
                }
            } ?: intent { reduce { CreateBookListingScreenState.Error("BookListing is null") } }
        }
    }

    internal fun getDeliveryMethod(map: Map<DeliveryMethod, Boolean>): DeliveryMethod {
        return if (map[DeliveryMethod.LocalPickup] == true && map[DeliveryMethod.Shipping] == true) {
            DeliveryMethod.LocalPickupAndShipping
        } else if (map[DeliveryMethod.LocalPickup] == true) {
            DeliveryMethod.LocalPickup
        } else {
            DeliveryMethod.Shipping
        }
    }

    fun navigateHome() {
        intent {
            postSideEffect(
                NavigationSideEffect.NavigateTo(
                    destination = NavigationDestination.PostFTS.HomeLanding,
                    popUpTo = true,
                    popUpToInclusive = true,
                )
            )
            reduce { CreateBookListingScreenState.Idle }
        }
    }
    
    fun onBackPressed() {
        intent {
            reduce {
                CreateBookListingScreenState.Idle
            }
        }
    }
}
