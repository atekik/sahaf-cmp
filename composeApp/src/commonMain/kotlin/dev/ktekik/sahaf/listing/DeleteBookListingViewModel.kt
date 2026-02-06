package dev.ktekik.sahaf.listing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ktekik.sahaf.navigation.NavigationSideEffect
import dev.ktekik.sahaf.usecases.DeleteBookListingResult
import dev.ktekik.sahaf.usecases.DeleteBookListingUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

sealed interface DeleteBookListingScreenState {
    data object Idle : DeleteBookListingScreenState
    data object Loading : DeleteBookListingScreenState
    data object Success : DeleteBookListingScreenState
    data class Error(val message: String) : DeleteBookListingScreenState
}

class DeleteBookListingViewModel(
    private val deleteBookListingUseCase: DeleteBookListingUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel(), ContainerHost<DeleteBookListingScreenState, NavigationSideEffect> {

    override val container: Container<DeleteBookListingScreenState, NavigationSideEffect> =
        container(DeleteBookListingScreenState.Idle)

    fun deleteListing(listingId: String) {
        intent {
            reduce { DeleteBookListingScreenState.Loading }
        }
        viewModelScope.launch(dispatcher) {
            deleteBookListingUseCase.execute(listingId).collect { result ->
                intent {
                    reduce {
                        when (result) {
                            is DeleteBookListingResult.Success -> {
                                DeleteBookListingScreenState.Success
                            }

                            is DeleteBookListingResult.Error -> {
                                DeleteBookListingScreenState.Error(result.message)
                            }
                        }
                    }
                }
            }
        }
    }
}
