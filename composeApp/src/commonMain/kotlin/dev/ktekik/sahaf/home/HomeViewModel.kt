package dev.ktekik.sahaf.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ktekik.sahaf.home.HomeScreenState.*
import dev.ktekik.sahaf.models.CursorPagedListings
import dev.ktekik.sahaf.models.Reader
import dev.ktekik.sahaf.navigation.NavigationSideEffect
import dev.ktekik.sahaf.usecases.FetchListingsParams
import dev.ktekik.sahaf.usecases.FetchListingsWithShippingUseCase
import dev.ktekik.sahaf.usecases.FetchReaderIdUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

sealed interface HomeScreenState {
    data object LoadingState : HomeScreenState
    data class ErrorState(val error: String) : HomeScreenState
    data class ReadyState(val reader: Reader, val listings: CursorPagedListings) : HomeScreenState
}

class HomeViewModel(
    private val fetchReaderIdUseCase: FetchReaderIdUseCase,
    private val fetchListingsWithShippingUseCase: FetchListingsWithShippingUseCase,
) : ViewModel(), ContainerHost<HomeScreenState, NavigationSideEffect> {

    override val container: Container<HomeScreenState, NavigationSideEffect> =
        container(LoadingState)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun fetchReader() {
        intent {
            reduce { LoadingState }
        }
        viewModelScope.launch(Dispatchers.IO) {
            delay(2000)
            fetchReaderIdUseCase.execute(Unit)
                .filterNotNull()
                .flatMapLatest { readerId ->
                    fetchListingsWithShippingUseCase.execute(
                        FetchListingsParams(
                            readerId = readerId,
                            excludeSelf = false
                        )
                    )
                }.collect { newState ->
                    withContext(Dispatchers.Main) {
                        intent {
                            reduce { newState }
                        }
                    }
                }
        }
    }
}
