package dev.ktekik.sahaf.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.intl.Locale
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.ktekik.sahaf.fts.GetStartedScreen
import dev.ktekik.sahaf.fts.RegistrationPendingDialog
import dev.ktekik.sahaf.fts.USZipcodeEntryScreen
import dev.ktekik.sahaf.fts.WelcomeScreen
import org.koin.compose.koinInject

sealed class RegionCode(val code: String) {
    object US: RegionCode("US")
    object Canada: RegionCode("CA")
    object Germany: RegionCode("DE")
    object UK: RegionCode("UK")
}

@Composable
fun NavHost() {
    val navController = rememberNavController()
    val viewModel: FtsNavigationViewModel = koinInject()
    val navState = viewModel.container.stateFlow.value

    val startDestination = navState.currentDestination
    // Todo also check if user info saved to database. If so, start with home screen. If not,

    NavHost(navController = navController, startDestination = startDestination.route) {
        composable(NavigationDestination.GetStarted.route) {
            GetStartedScreen(viewModel, navController)
        }
        composable(NavigationDestination.Welcome.route) {
            WelcomeScreen(viewModel, navController)
        }
        composable(
            route = NavigationDestination.ZipcodeEntry.route,
        ) {
            when(Locale.current.region) {
                RegionCode.US.code -> USZipcodeEntryScreen(viewModel, navController)
                else -> TODO("Not yet implemented")
            }
        }
        composable(
            route = NavigationDestination.RegistrationPendingDialog.route,
        ) {
            RegistrationPendingDialog(viewModel)
        }
    }
}
