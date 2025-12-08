package dev.ktekik.sahaf.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.intl.Locale
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import dev.ktekik.sahaf.fts.GetStartedScreen
import dev.ktekik.sahaf.fts.RegistrationFailedDialog
import dev.ktekik.sahaf.fts.RegistrationPendingDialog
import dev.ktekik.sahaf.fts.USZipcodeEntryScreen
import dev.ktekik.sahaf.fts.WelcomeScreen
import dev.ktekik.sahaf.home.HomeScreen
import dev.ktekik.sahaf.reader.ReaderRegistryViewModel
import org.koin.compose.koinInject

sealed class RegionCode(val code: String) {
    object US : RegionCode("US")
    object Canada : RegionCode("CA")
    object Germany : RegionCode("DE")
    object UK : RegionCode("UK")
}

@Composable
fun FtsNavHost() {
    val navController = rememberNavController()
    val viewModel: FtsNavigationViewModel = koinInject()
    val readerRegistryViewModel: ReaderRegistryViewModel = koinInject()
    val navState = viewModel.container.stateFlow.value
    // Todo also check if user info saved to database. If so, start with home screen. If not,
    val startDestination = navState.destination

    RouteObserver(navController)

    NavHost(navController = navController, startDestination = startDestination.route) {
        composable(NavigationDestination.GetStarted.route) {
            GetStartedScreen()
        }
        composable(NavigationDestination.Welcome.route) {
            WelcomeScreen()
        }
        composable(
            route = NavigationDestination.ZipcodeEntry.route,
        ) {
            when (Locale.current.region) {
                RegionCode.US.code -> USZipcodeEntryScreen()
                else -> TODO("Not yet implemented")
            }
        }
        composable(
            route = NavigationDestination.RegistrationPendingDialog.route,
        ) {
            RegistrationPendingDialog(readerRegistryViewModel)
        }

        composable(
            route = NavigationDestination.RegistrationFailedDialog.route
        ) {
            RegistrationFailedDialog(readerRegistryViewModel = readerRegistryViewModel)
        }

        navigation(
            route = NavigationDestination.Home.route,
            startDestination = NavigationDestination.PostFTS.HomeLanding.homeRoute
        ) {
            composable(
                route = NavigationDestination.PostFTS.HomeLanding.homeRoute
            ) {
                HomeScreen()
            }
        }

    }
}

@Composable
fun RouteObserver(navController: NavController) {
    val viewModel: FtsNavigationViewModel = koinInject()

    LaunchedEffect(Unit) {
        viewModel.container.sideEffectFlow.collect { sideEffect ->
            when (sideEffect) {
                is NavigationSideEffect.NavigateTo -> {
                    navController.navigate(
                        route = sideEffect.destination.route,
                    ) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }
}
