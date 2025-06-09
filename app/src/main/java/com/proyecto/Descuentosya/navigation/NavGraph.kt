package com.proyecto.Descuentosya.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.proyecto.Descuentosya.home.BilleterasScreen
import com.proyecto.Descuentosya.home.MisDescuentosScreen
import com.proyecto.Descuentosya.home.WelcomeScreen
import com.proyecto.Descuentosya.login.AccountScreen
import com.proyecto.Descuentosya.login.ForgotPasswordScreen
import com.proyecto.Descuentosya.login.LoginScreen
import com.proyecto.Descuentosya.login.RegisterScreen
import com.proyecto.Descuentosya.profile.AppearanceScreen
import com.proyecto.Descuentosya.profile.SettingsScreen
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

        composable("forgot_password") {
            ForgotPasswordScreen(navController)
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
