package com.proyecto.descuentosya.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.descuentosya.ui.screens.LoginScreen
import com.example.descuentosya.ui.screens.RegisterScreen
import com.proyecto.descuentosya.ui.screens.WelcomeScreen
import com.proyecto.descuentosya.ui.screens.BilleterasScreen
import com.proyecto.descuentosya.ui.screens.MisDescuentosScreen
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        composable("welcome") {
            WelcomeScreen(navController = navController)
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
    }
}