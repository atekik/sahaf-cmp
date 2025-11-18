package dev.ktekik.sahaf.navigation

sealed class NavigationDestination(val route: String) {
    data object GetStarted : NavigationDestination("fts_get_started")
    data object Welcome : NavigationDestination("fts_greet")
    data object ZipcodeEntry : NavigationDestination("zipcode_entry")
    data object RegistrationPendingDialog : NavigationDestination("registration_pending")
    data object Home : NavigationDestination("home")
}

