package com.proyecto.Descuentosya.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.proyecto.Descuentosya.home.BilleterasScreen
import com.proyecto.Descuentosya.home.BilleteraDetailScreen
import com.proyecto.Descuentosya.home.MisDescuentosScreen
import com.proyecto.Descuentosya.home.WelcomeScreen
import com.proyecto.Descuentosya.login.*
import com.proyecto.Descuentosya.profile.AppearanceScreen
import com.proyecto.Descuentosya.profile.SettingsScreen
import com.proyecto.Descuentosya.viewmodel.BilleterasViewModel
import com.proyecto.Descuentosya.viewmodel.ThemeViewModel
import com.proyecto.Descuentosya.notification.NotificationsScreen
import com.proyecto.Descuentosya.profile.EditProfileScreen
import com.proyecto.Descuentosya.home.CartScreen
import com.proyecto.Descuentosya.home.SearchScreen


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
        composable("billeteras_favoritas") { MisDescuentosScreen(navController = navController) }
        composable("account") { AccountScreen(navController) }

        composable("cart") { CartScreen(navController = navController) }
        composable("search") { SearchScreen(navController = navController) }
        composable("notifications") { NotificationsScreen(navController = navController) }

        composable("edit_profile") { EditProfileScreen(navController = navController) }
        composable("appearance") {
            AppearanceScreen(
                navController = navController,
                isDarkMode = themeViewModel.isDarkTheme.collectAsState().value,
                onThemeToggle = { themeViewModel.setDarkTheme(it) }
            )
        }

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
