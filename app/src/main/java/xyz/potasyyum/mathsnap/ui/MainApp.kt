package xyz.potasyyum.mathsnap.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import xyz.potasyyum.mathsnap.ui.dashboard.DashboardScreen
import xyz.potasyyum.mathsnap.ui.dashboard.DashboardViewModel

@Composable
fun MainApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Route.DASHBOARD
    ) {
        composable(Route.DASHBOARD) { backStackEntry ->
            val viewModel = hiltViewModel<DashboardViewModel>()
            val uiState = viewModel.uiState
            DashboardScreen(uiState)
        }
    }

}

object Route {
    const val DASHBOARD = "dashboard"
}