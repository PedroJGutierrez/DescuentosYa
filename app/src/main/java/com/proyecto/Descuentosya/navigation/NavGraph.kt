package com.proyecto.Descuentosya.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.proyecto.Descuentosya.home.BilleterasScreen
import com.proyecto.Descuentosya.home.BilleteraDetailScreen
import com.proyecto.Descuentosya.home.MapaScreen
import com.proyecto.Descuentosya.home.NearbyCinemasMapScreen
import com.proyecto.Descuentosya.home.NearbyRestaurantsMapScreen
import com.proyecto.Descuentosya.home.NearbySupermarketsMapScreen
import com.proyecto.Descuentosya.home.WelcomeScreen
import com.proyecto.Descuentosya.login.*
import com.proyecto.Descuentosya.profile.AppearanceScreen
import com.proyecto.Descuentosya.profile.SettingsScreen
import com.proyecto.Descuentosya.viewmodel.BilleterasViewModel
import com.proyecto.Descuentosya.viewmodel.ThemeViewModel
import com.proyecto.Descuentosya.notification.NotificationsScreen
import com.proyecto.Descuentosya.profile.AccountsScreen
import com.proyecto.Descuentosya.profile.EditProfileScreen
import com.proyecto.Descuentosya.profile.PrivacyScreen
import com.proyecto.Descuentosya.profile.SecurityScreen


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

        composable("notifications") {
            NotificationsScreen(navController)
        }
        composable("edit_profile?focusField={focusField}") { backStackEntry ->
            EditProfileScreen(navController)
        }
        composable("privacy") {
            PrivacyScreen(navController)
        }
        composable("accounts") {
            AccountsScreen(navController)
        }
        composable("mapa")//este es para restaurantes map
        {
            NearbyRestaurantsMapScreen()
        }
        composable("mapa_cine") {
            NearbyCinemasMapScreen()
        }
        composable("mapa_super") {
            NearbySupermarketsMapScreen()
        }

        composable("security") { SecurityScreen(navController) }
        // NUEVA PANTALLA
        composable("billetera_detalle/{nombre}") { backStackEntry ->
            val billeteraNombre = backStackEntry.arguments?.getString("nombre") ?: ""
            val viewModel: BilleterasViewModel = viewModel()
            val billeteras = viewModel.billeteras.collectAsState().value
            val billetera = billeteras.find { it.nombre == billeteraNombre }

            billetera?.let {
                BilleteraDetailScreen(billetera = it, navController = navController)
            }
        }
    }
}
