package com.proyecto.Descuentosya.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.descuentosya.ui.screens.LoginScreen
import com.example.descuentosya.ui.screens.RegisterScreen
import com.example.descuentosya.ui.screens.SettingsScreen
import com.example.descuentosya.ui.screens.WelcomeScreen
import com.proyecto.Descuentosya.ui.screens.BilleterasScreen
import com.proyecto.Descuentosya.ui.screens.MisDescuentosScreen
import com.example.descuentosya.ui.screens.AppearanceScreen
import com.example.descuentosya.ui.screens.AccountScreen
import com.proyecto.descuentosya.viewmodel.ThemeViewModel

@Composable
fun NavGraph(navController: NavHostController, themeViewModel: ThemeViewModel) {
    NavHost(
        navController = navController,
        startDestination = "welcome"
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
        composable("account") { AccountScreen(navController) }
        composable("appearance") {
            AppearanceScreen(
                navController = navController,
                isDarkMode = themeViewModel.isDarkTheme.collectAsState().value,
                onThemeToggle = { themeViewModel.setDarkTheme(it) }
            )
        }
    }
}