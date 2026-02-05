package dev.ktekik.sahaf.navigation

sealed class NavigationDestination(open val route: String) {
    data object Splash :  NavigationDestination("splash")
    data object GetStarted : NavigationDestination("fts_get_started")
    data object Welcome : NavigationDestination("fts_greet")
    data object ZipcodeEntry : NavigationDestination("zipcode_entry")
    data object RegistrationPendingDialog : NavigationDestination("registration_pending")
    data object RegistrationFailedDialog : NavigationDestination("registration_failed")

    data object Home : NavigationDestination("home")
    sealed class PostFTS(val homeRoute: String) : NavigationDestination(homeRoute) {
        data object HomeLanding : PostFTS("home_landing")

        data object ListingDetail : PostFTS("listing_detail/{listingId}") {
            fun createRoute(listingId: String) = "listing_detail/$listingId"
        }

        data object BookListing : PostFTS("book_listing/{isbn}") {
            fun createRoute(isbn: String) = "book_listing/$isbn"
        }
    }
}

