package com.proyecto.Descuentosya.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proyecto.Descuentosya.components.DataManager
import com.proyecto.Descuentosya.data.FavoritosManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BilleterasScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val authToken = sharedPreferences.getString("auth_token", null)

    // Estado del Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val showSnackbar = remember { mutableStateOf(false) }
    val mensajeSnackbar = remember { mutableStateOf("") }

    if (authToken == null) {
        LaunchedEffect(Unit) {
            navController.navigate("login") {
                popUpTo("billeteras") { inclusive = true }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Billeteras") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Explora tus billeteras:", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(DataManager.billeteras) { billetera ->
                    Card(
                        modifier = Modifier
                            .width(250.dp)
                            .height(150.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = billetera,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Button(
                                onClick = {
                                    // Agregar a favoritos
                                    FavoritosManager.agregarFavorito(billetera)
                                    // Establecer el mensaje y activar el Snackbar
                                    mensajeSnackbar.value = "¡$billetera agregado a favoritos!"
                                    showSnackbar.value = true
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Agregar a Favoritos")
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { navController.navigate("welcome") },
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
            ) {
                Text("Volver al inicio")
            }
        }

        // Mostrar Snackbar solo si el estado está activado
        if (showSnackbar.value) {
            LaunchedEffect(mensajeSnackbar.value) {
                snackbarHostState.showSnackbar(mensajeSnackbar.value)
                showSnackbar.value = false // Resetear el estado para que no se repita
            }
        }
    }
}
