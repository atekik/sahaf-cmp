package dev.ktekik.sahaf.navigation

import androidx.lifecycle.ViewModel
import dev.ktekik.sahaf.cloud.Reader
import dev.ktekik.signin.models.Profile
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import kotlin.uuid.ExperimentalUuidApi

data class NavigationState(
    val currentDestination: NavigationDestination = NavigationDestination.GetStarted,
    val profile: Profile? = null
)

sealed interface NavigationSideEffect {
    data class NavigateTo(val destination: NavigationDestination, val profile: Profile? = null) : NavigationSideEffect
}

class FtsNavigationViewModel : ViewModel(), ContainerHost<NavigationState, NavigationSideEffect> {
    override val container: Container<NavigationState, NavigationSideEffect> =
        container<NavigationState, NavigationSideEffect>(NavigationState())

    fun navigateToGreeting() {
        intent {
            reduce { state.copy(currentDestination = NavigationDestination.Welcome) }
            postSideEffect(NavigationSideEffect.NavigateTo(NavigationDestination.Welcome))
        }
    }

    fun navigateToZipcodeEntry(profile: Profile) {
        intent {
            reduce { state.copy(currentDestination = NavigationDestination.ZipcodeEntry, profile = profile) }
            postSideEffect(NavigationSideEffect.NavigateTo(NavigationDestination.ZipcodeEntry, profile))
        }
    }

    fun registerProfile(profile: Profile) {
        intent {
            reduce { state.copy(currentDestination = NavigationDestination.RegistrationPendingDialog, profile = profile) }
            postSideEffect(NavigationSideEffect.NavigateTo(NavigationDestination.RegistrationPendingDialog, profile))
        }
    }
}
