package com.proyecto.descuentosya.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.proyecto.descuentosya.ui.screens.LoginScreen
import com.proyecto.descuentosya.ui.screens.RegisterScreen
import com.proyecto.descuentosya.ui.screens.WelcomeScreen
import com.proyecto.descuentosya.ui.theme.screen.BilleterasScreen
import com.proyecto.descuentosya.ui.theme.screen.MisDescuentosScreen
import com.proyecto.descuentosya.ui.theme.screen.WelcomeScreenLogged

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        composable("welcome") {
            WelcomeScreen(navController)
        }
        composable("login") {
            LoginScreen(navController)
        }
        composable("register") {
            RegisterScreen(navController)
        }
        composable("home") {
            // Aquí iría tu pantalla de inicio o "home"
        }
        composable("welcome_logged") {
            WelcomeScreenLogged(navController)
        }
        composable("billeteras") {
            BilleterasScreen(navController)
        }
        composable("mis_descuentos") {
            MisDescuentosScreen()
        }
    }
}

