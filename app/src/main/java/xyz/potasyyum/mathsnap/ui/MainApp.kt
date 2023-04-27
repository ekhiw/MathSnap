package xyz.potasyyum.mathsnap.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import xyz.potasyyum.mathsnap.ui.dashboard.DashboardScreen

@Composable
fun MainApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Route.DASHBOARD
    ) {
        composable(Route.DASHBOARD) { backStackEntry ->
            DashboardScreen()
        }
    }

}

object Route {
    const val DASHBOARD = "dashboard"
}