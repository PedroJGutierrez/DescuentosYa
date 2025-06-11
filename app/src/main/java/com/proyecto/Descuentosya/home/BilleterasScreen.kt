package com.proyecto.Descuentosya.home

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proyecto.Descuentosya.components.Billetera
import com.proyecto.Descuentosya.components.DataManager
import com.proyecto.Descuentosya.data.FavoritosManager
import com.proyecto.Descuentosya.ui.theme.FondoCelesteBackground
import com.proyecto.Descuentosya.ui.theme.BannerCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BilleterasScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val authToken = sharedPreferences.getString("auth_token", null)

    val snackbarHostState = remember { SnackbarHostState() }
    val showSnackbar = remember { mutableStateOf(false) }
    val mensajeSnackbar = remember { mutableStateOf("") }

    var filtroAbierto by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        FavoritosManager.cargarFavoritosDesdeFirestore()
    }

    if (authToken == null) {
        LaunchedEffect(Unit) {
            navController.navigate("login") {
                popUpTo("billeteras") { inclusive = true }
            }
        }
        return
    }

    val favoritosCargados = FavoritosManager.favoritosCargados

    if (!favoritosCargados.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val billeterasOrdenadas = remember {
        DataManager.billeteras.sortedWith(
            compareByDescending<Billetera> { billetera ->
                billetera.beneficios.any { it.disponible }
            }.thenBy { it.nombre }
        )
    }

    FondoCelesteBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Mis Billeteras")
                            IconButton(onClick = { filtroAbierto = !filtroAbierto }) {
                                Icon(Icons.Default.FilterList, contentDescription = "Filtrar")
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0f)
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (filtroAbierto) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            billeterasOrdenadas.forEach { billetera ->
                                Text(text = "• ${billetera.nombre}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text("Explora tus billeteras:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(billeterasOrdenadas) { billetera ->
                        BannerCard(
                            billetera = billetera,
                            navController = navController,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            onFavoritoCambiado = { billeteraModificada, nuevoEstado ->
                                mensajeSnackbar.value = if (nuevoEstado)
                                    "$billeteraModificada agregado a favoritos"
                                else
                                    "$billeteraModificada eliminado de favoritos"
                                showSnackbar.value = true
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate("welcome") },
                    modifier = Modifier.fillMaxWidth()
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
}

