package dev.ktekik.sahaf.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.ktekik.sahaf.fts.WelcomeScreen
import dev.ktekik.sahaf.fts.GetStartedScreen
import org.koin.compose.koinInject

@Composable
fun NavHost() {
    val navController = rememberNavController()
    val viewModel: FtsNavigationViewModel = koinInject()

    NavHost(navController = navController, startDestination = NavigationDestination.GetStarted.route) {
        composable(NavigationDestination.GetStarted.route) {
            GetStartedScreen(viewModel, navController)
        }
        composable(NavigationDestination.Welcome.route) {
            WelcomeScreen()
        }
    }
}

