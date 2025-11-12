package dev.ktekik.sahaf.navigation

import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

data class NavigationState(
    val currentDestination: NavigationDestination = NavigationDestination.GetStarted
)

sealed interface NavigationSideEffect {
    data class NavigateTo(val destination: NavigationDestination) : NavigationSideEffect
}

class FtsNavigationViewModel : ViewModel(), ContainerHost<NavigationState, NavigationSideEffect> {
    override val container: Container<NavigationState, NavigationSideEffect> =
        container<NavigationState, NavigationSideEffect>(NavigationState())

    fun navigateToGreeting() {
        intent {
            postSideEffect(NavigationSideEffect.NavigateTo(NavigationDestination.Welcome))
        }
    }
}

