package dev.ktekik.sahaf.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ktekik.sahaf.models.Reader
import dev.ktekik.sahaf.navigation.NavigationSideEffect
import dev.ktekik.sahaf.usecases.FetchReaderIdUseCase
import dev.ktekik.sahaf.usecases.QueryReaderUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

sealed interface HomeScreenState {
    data object LoadingState : HomeScreenState
    data class ErrorState(val error: String?) : HomeScreenState
    data class ReadyState(val reader: Reader) : HomeScreenState
}

class HomeViewModel(
    private val fetchReaderIdUseCase: FetchReaderIdUseCase,
    private val queryReaderUseCase: QueryReaderUseCase,
) : ViewModel(), ContainerHost<HomeScreenState, NavigationSideEffect> {

    override val container: Container<HomeScreenState, NavigationSideEffect> =
        container(HomeScreenState.LoadingState)

    init {
        fetchReader()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchReader() {
        viewModelScope.launch {
            fetchReaderIdUseCase.execute(Unit)
                .filterNotNull()
                .flatMapLatest { readerId ->
                    queryReaderUseCase.execute(readerId)
                }.collect { newState ->
                    intent {
                        reduce { newState }
                    }
                }
        }
    }
}
