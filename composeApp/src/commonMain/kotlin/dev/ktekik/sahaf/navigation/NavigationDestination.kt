package dev.ktekik.sahaf.navigation

sealed class NavigationDestination(val route: String) {
    data object Splash :  NavigationDestination("splash")
    data object GetStarted : NavigationDestination("fts_get_started")
    data object Welcome : NavigationDestination("fts_greet")
    data object ZipcodeEntry : NavigationDestination("zipcode_entry")
    data object RegistrationPendingDialog : NavigationDestination("registration_pending")
    data object RegistrationFailedDialog : NavigationDestination("registration_failed")

    data object Home : NavigationDestination("home")
    sealed class PostFTS(val homeRoute: String) : NavigationDestination("post_fts") {
        data object HomeLanding : PostFTS("home_landing")
    }
}

