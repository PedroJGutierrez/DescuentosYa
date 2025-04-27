package com.proyecto.descuentosya.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.proyecto.descuentosya.ui.theme.components.DataManager
import com.proyecto.descuentosya.R

@Composable
fun WelcomeScreen(navController: NavController) {
    val context = LocalContext.current
    val welcomeViewModel: WelcomeViewModel = viewModel()

    // Collecting the state in the Composable function
    val isLoggedIn by welcomeViewModel.isLoggedIn.collectAsState(initial = false)

    // Verificar el estado de la sesión
    LaunchedEffect(key1 = context) {
        welcomeViewModel.checkAuthToken(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Bienvenido a Descuentos Ya",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoggedIn) {
            // Versión para usuarios autenticados
            Text(
                "Sesión iniciada correctamente",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Button(
                onClick = { navController.navigate("billeteras") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text("Billeteras")
            }

            Button(
                onClick = { navController.navigate("billeteras_favoritas") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text("Billeteras Favoritas")
            }

            Button(
                onClick = {
                    welcomeViewModel.logout(context)
                    navController.navigate("welcome") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Cerrar Sesión")
            }
        } else {
            // Versión para usuarios no autenticados
            Text("Lo que dicen nuestros usuarios:", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(listOf(R.drawable.persona1, R.drawable.persona2, R.drawable.persona3)) { image ->
                    Card(
                        modifier = Modifier
                            .width(250.dp)
                            .height(300.dp)
                    ) {
                        Image(
                            painter = painterResource(id = image),
                            contentDescription = "Usuario feliz con celular",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("login") },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Iniciar Sesión")
            }

            OutlinedButton(
                onClick = { navController.navigate("register") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text("Registrarse")
            }
        }
    }
}
