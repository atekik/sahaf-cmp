package dev.ktekik.sahaf.listing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ktekik.sahaf.models.Book
import dev.ktekik.sahaf.navigation.NavigationSideEffect
import dev.ktekik.sahaf.usecases.IsbnQueryResult
import dev.ktekik.sahaf.usecases.IsbnQueryUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

sealed interface BookListingScreenState {
    data object Loading : BookListingScreenState
    data class Ready(val book: Book) : BookListingScreenState
    data class Error(val message: String) : BookListingScreenState
}

class IsbnQueryViewModel(
    private val isbnQueryUseCase: IsbnQueryUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel(), ContainerHost<BookListingScreenState, NavigationSideEffect> {

    override val container: Container<BookListingScreenState, NavigationSideEffect> =
        container(BookListingScreenState.Loading)

    fun fetchBook(isbn: String) {
        println("Fetching book for ISBN: $isbn")
        viewModelScope.launch(dispatcher) {
            isbnQueryUseCase.execute(isbn).collect { result ->
                intent {
                    reduce {
                        when (result) {
                            is IsbnQueryResult.Success -> BookListingScreenState.Ready(result.book)
                            is IsbnQueryResult.Error -> BookListingScreenState.Error(result.message)
                        }
                    }
                }
            }
        }
    }
}
