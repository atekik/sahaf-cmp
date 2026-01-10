package dev.ktekik.sahaf.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ktekik.sahaf.usecases.FetchReaderIdUseCase
import dev.ktekik.signin.models.Profile
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class NavigationViewModel(fetchReaderIdUseCase: FetchReaderIdUseCase) : ViewModel(),
    ContainerHost<NavigationState, NavigationSideEffect> {
    override val container: Container<NavigationState, NavigationSideEffect> =
        container(NavigationState())

    init {
        viewModelScope.launch {
            fetchReaderIdUseCase.execute(Unit).collect {
                if (it == null) {
                    navigateToGetStarted()
                } else {
                    navigateHome()
                }
            }
        }
    }

    fun navigateToGetStarted() {
        intent {
            postSideEffect(NavigationSideEffect.NavigateTo(NavigationDestination.GetStarted))
        }
    }

    fun navigateToGreeting() {
        intent {
            reduce { state.copy(destination = NavigationDestination.Welcome) }
            postSideEffect(NavigationSideEffect.NavigateTo(NavigationDestination.Welcome))
        }
    }

    fun navigateToZipcodeEntry(profile: Profile) {
        intent {
            reduce {
                state.copy(
                    destination = NavigationDestination.ZipcodeEntry,
                    profile = profile
                )
            }
            postSideEffect(
                NavigationSideEffect.NavigateTo(
                    destination = NavigationDestination.ZipcodeEntry,
                    popUpTo = true,
                    popUpToInclusive = true
                )
            )
        }
    }

    fun registerProfile(profile: Profile) {
        intent {
            reduce {
                state.copy(
                    destination = NavigationDestination.RegistrationPendingDialog,
                    profile = profile
                )
            }
            postSideEffect(
                NavigationSideEffect.NavigateTo(
                    NavigationDestination.RegistrationPendingDialog,
                    popUpTo = true,
                    popUpToInclusive = true
                )
            )
        }
    }

    fun navigateHome() {
        intent {
            postSideEffect(
                NavigationSideEffect.NavigateTo(
                    NavigationDestination.Home,
                    popUpTo = true,
                    popUpToInclusive = true
                )
            )
        }
    }

    fun navigateToRegistrationFailed() {
        intent {
            postSideEffect(
                NavigationSideEffect.NavigateTo(
                    NavigationDestination.RegistrationFailedDialog,
                    popUpTo = true,
                    popUpToInclusive = true
                )
            )
        }
    }
    fun onIsbnScanned(isbn: String) {
        intent {
            postSideEffect(
                NavigationSideEffect.NavigateTo(
                    destination = NavigationDestination.PostFTS.BookListing.apply {
                        createRoute(isbn)
                    }
                )
            )
        }
    }
}
