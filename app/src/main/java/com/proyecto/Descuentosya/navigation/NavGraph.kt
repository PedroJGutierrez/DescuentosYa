package com.proyecto.Descuentosya.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.descuentosya.ui.screens.*
import com.proyecto.Descuentosya.ui.screens.BilleterasScreen
import com.proyecto.Descuentosya.ui.screens.MisDescuentosScreen
import com.proyecto.Descuentosya.viewmodel.ThemeViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    themeViewModel: ThemeViewModel,
    startDestination: String = "welcome"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("welcome") {
            WelcomeScreen(navController = navController)
        }

        composable("settings") {
            SettingsScreen(navController)
        }

        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("register") {
            RegisterScreen(navController = navController)
        }

        composable("billeteras") {
            BilleterasScreen(navController = navController)
        }

        composable("billeteras_favoritas") {
            MisDescuentosScreen(navController = navController)
        }

        composable("account") {
            AccountScreen(navController)
        }

        composable("appearance") {
            AppearanceScreen(
                navController = navController,
                isDarkMode = themeViewModel.isDarkTheme.collectAsState().value,
                onThemeToggle = { themeViewModel.setDarkTheme(it) }
            )
        }
    }
}
