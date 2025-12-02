package dev.ktekik.sahaf.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.ktekik.sahaf.fts.GetStartedScreen
import dev.ktekik.sahaf.fts.RegistrationFailedDialog
import dev.ktekik.sahaf.fts.RegistrationPendingDialog
import dev.ktekik.sahaf.fts.USZipcodeEntryScreen
import dev.ktekik.sahaf.fts.WelcomeScreen
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
    val startDestination = navState.currentDestination

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

        composable(
            route = NavigationDestination.Home.route
        ) {
            HomeScreenPlaceHolder()
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

@Composable
private fun HomeScreenPlaceHolder() {
    val viewModel: FtsNavigationViewModel = koinInject()
    val state: NavigationState = viewModel.container.stateFlow.value

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
    ) {
        Text(
            "Name: ${state.profile?.name}",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Family Name: ${state.profile?.familyName}",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Given Name: ${state.profile?.givenName}",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Email: ${state.profile?.email}",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Picture: ${state.profile?.picture}",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Zipcode: ${state.profile?.zipcode}",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
    }
}
