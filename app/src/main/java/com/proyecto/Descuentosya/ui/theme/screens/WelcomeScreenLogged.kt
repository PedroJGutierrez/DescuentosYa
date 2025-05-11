package com.proyecto.Descuentosya.ui.theme.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proyecto.Descuentosya.auth.AuthManager
@Composable
fun WelcomeScreenLogged(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Bienvenido, ${AuthManager.currentUser?.username}", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("billeteras") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Billeteras")
        }

        Button(
            onClick = { navController.navigate("mis_descuentos") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mis Descuentos")
        }

        OutlinedButton(
            onClick = {
                AuthManager.logout()
                navController.navigate("welcome") {
                    popUpTo("welcome_logged") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}
