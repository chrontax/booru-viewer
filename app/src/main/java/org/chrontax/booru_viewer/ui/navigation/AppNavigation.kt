package org.chrontax.booru_viewer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.chrontax.booru_viewer.ui.screens.home.HomeScreen

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = AppDestination.Home.route) {
        composable(AppDestination.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(AppDestination.Settings.route) {
            // SettingsScreen(navController)
        }
    }
}