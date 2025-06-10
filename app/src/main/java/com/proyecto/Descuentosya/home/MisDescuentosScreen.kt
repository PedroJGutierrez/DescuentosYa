package com.proyecto.Descuentosya.home

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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proyecto.Descuentosya.components.Billetera
import com.proyecto.Descuentosya.data.FavoritosManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisDescuentosScreen(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val showSnackbar = remember { mutableStateOf(false) }
    val mensajeSnackbar = remember { mutableStateOf("") }

    var billeterasFavoritas by remember { mutableStateOf<List<Billetera>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Cargar favoritos desde Firestore
    LaunchedEffect(Unit) {
        FavoritosManager.cargarFavoritosDesdeFirestore()
        billeterasFavoritas = FavoritosManager.obtenerBilleterasFavoritas()
        isLoading = false
    }

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
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }
                billeterasFavoritas.isEmpty() -> {
                    Text("Todavía no tienes billeteras favoritas.", style = MaterialTheme.typography.bodyLarge)
                }
                else -> {
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
                                            text = billetera.nombre,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            billetera.beneficios.forEach { beneficio ->
                                                Icon(
                                                    imageVector = beneficio.icon,
                                                    contentDescription = beneficio.descripcion,
                                                    tint = if (beneficio.disponible) Color(0xFF4CAF50) else Color.Gray,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                    }

                                    IconButton(
                                        onClick = {
                                            FavoritosManager.quitarFavorito(billetera.nombre)
                                            billeterasFavoritas = billeterasFavoritas.filterNot { it.nombre == billetera.nombre }
                                            mensajeSnackbar.value = "${billetera.nombre} eliminado"
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
            }

            Button(
                onClick = { navController.navigate("welcome") },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Volver al inicio")
            }
        }

        if (showSnackbar.value) {
            LaunchedEffect(mensajeSnackbar.value) {
                snackbarHostState.showSnackbar(mensajeSnackbar.value)
                showSnackbar.value = false
            }
        }
    }
}
