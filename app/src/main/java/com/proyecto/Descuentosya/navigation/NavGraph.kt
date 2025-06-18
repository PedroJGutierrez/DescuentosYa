package com.proyecto.Descuentosya.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.proyecto.Descuentosya.home.*
import com.proyecto.Descuentosya.login.*
import com.proyecto.Descuentosya.notification.NotificationsScreen
import com.proyecto.Descuentosya.profile.*
import com.proyecto.Descuentosya.viewmodel.BilleterasViewModel
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
        composable("welcome") { WelcomeScreen(navController = navController) }
        composable("settings") { SettingsScreen(navController) }
        composable("forgot_password") { ForgotPasswordScreen(navController) }
        composable("login") { LoginScreen(navController = navController) }
        composable("register") { RegisterScreen(navController = navController) }
        composable("billeteras") { BilleterasScreen(navController = navController) }

        composable("account") { AccountsScreen(navController) }
        composable("notifications") { NotificationsScreen(navController = navController) }

        composable("edit_profile") { EditProfileScreen(navController = navController) }
        composable("appearance") {
            AppearanceScreen(
                navController = navController,
                isDarkMode = themeViewModel.isDarkTheme.collectAsState().value,
                onThemeToggle = { themeViewModel.setDarkTheme(it) }
            )
        }

        composable("edit_profile?focusField={focusField}") { backStackEntry ->
            EditProfileScreen(navController)
        }

        composable("privacy") { PrivacyScreen(navController) }
        composable("accounts") { AccountsScreen(navController) }
        composable("mapa") { NearbyRestaurantsMapScreen() }
        composable("mapa_cine") { NearbyCinemasMapScreen() }
        composable("mapa_super") { NearbySupermarketsMapScreen() }

        composable("mapa_comercio/{nombre}") { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
            MapaScreen(nombreComercio = nombre)
        }

        composable("security") { SecurityScreen(navController) }

        // PANTALLA DE DETALLE DE BILLETERA CON MANEJO DE CARGA
        composable(
            route = "billetera_detalle/{nombre}",
            arguments = listOf(navArgument("nombre") { defaultValue = "" }),
            deepLinks = listOf(navDeepLink {
                uriPattern = "descuentosya://billetera_detalle/{nombre}"
            })
        ) { backStackEntry ->
            val billeteraNombre = backStackEntry.arguments?.getString("nombre")?.trim() ?: ""
            val viewModel: BilleterasViewModel = viewModel()
            val billeteras by viewModel.billeteras.collectAsState()
            val isLoading by viewModel.isLoading.collectAsState()

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val billetera = billeteras.find {
                    it.nombre.trim().equals(billeteraNombre, ignoreCase = true)
                }

                billetera?.let {
                    BilleteraDetailScreen(billetera = it, navController = navController)
                } ?: run {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        androidx.compose.material3.Text("Billetera no encontrada: \"$billeteraNombre\"")
                    }
                }
            }
        }

    }
}
