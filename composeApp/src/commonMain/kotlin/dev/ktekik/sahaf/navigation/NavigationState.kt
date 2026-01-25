package dev.ktekik.sahaf.navigation

import dev.ktekik.signin.models.Profile

data class NavigationState(
    val destination: NavigationDestination = NavigationDestination.GetStarted,
    val profile: Profile? = null
)

sealed interface NavigationSideEffect {
    data class NavigateTo(
        val destination: NavigationDestination,
        val route: String = destination.route,
        val popUpTo: Boolean = false,
        val popUpToInclusive: Boolean = false,
    ) : NavigationSideEffect
}