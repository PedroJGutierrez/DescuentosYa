package com.proyecto.Descuentosya.home

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proyecto.Descuentosya.data.FavoritosManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisDescuentosScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val authToken = sharedPreferences.getString("auth_token", null)

    val snackbarHostState = remember { SnackbarHostState() }
    val showSnackbar = remember { mutableStateOf(false) }
    val mensajeSnackbar = remember { mutableStateOf("") }

    if (authToken == null) {
        LaunchedEffect(Unit) {
            navController.navigate("login") {
                popUpTo("billeteras_favoritas") { inclusive = true }
            }
        }
        return
    }

    // Cargar favoritos desde Firestore al entrar
    LaunchedEffect(Unit) {
        FavoritosManager.cargarFavoritosDesdeFirestore(context)
    }

    // Leer favoritos reactivos
    val billeterasFavoritas by remember { derivedStateOf { FavoritosManager.favoritos.toList() } }

    val descuentos = mapOf(
        "Mercado Pago" to "20% OFF en restaurantes",
        "Ualá" to "10% OFF en supermercados",
        "BBVA" to "15% OFF en ropa",
        "Brubank" to "5% OFF en tecnología",
        "Galicia" to "30% OFF en viajes",
        "Santander" to "25% OFF en bares"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Billeteras Favoritas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (billeterasFavoritas.isEmpty()) {
                Text("Todavía no tienes billeteras favoritas.", style = MaterialTheme.typography.bodyLarge)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(billeterasFavoritas) { billetera ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = billetera,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = descuentos[billetera] ?: "Descuento exclusivo disponible",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        FavoritosManager.quitarFavorito(context, billetera)
                                        mensajeSnackbar.value = "$billetera eliminado"
                                        showSnackbar.value = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { navController.navigate("welcome") },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Volver al inicio")
            }
        }

        // Mostrar Snackbar si corresponde
        if (showSnackbar.value) {
            LaunchedEffect(mensajeSnackbar.value) {
                snackbarHostState.showSnackbar(mensajeSnackbar.value)
                showSnackbar.value = false
            }
        }
    }
}
