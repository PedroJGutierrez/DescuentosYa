package com.proyecto.Descuentosya.home

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.proyecto.Descuentosya.viewmodel.BilleterasViewModel
import com.proyecto.Descuentosya.data.FavoritosManager
import com.proyecto.Descuentosya.components.Billetera
import com.proyecto.Descuentosya.ui.theme.*
import com.proyecto.Descuentosya.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BilleterasScreen(
    navController: NavController,
    viewModel: BilleterasViewModel = viewModel(),
    themeViewModel: ThemeViewModel = viewModel()
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val authToken = sharedPreferences.getString("auth_token", null)

    val snackbarHostState = remember { SnackbarHostState() }
    val showSnackbar = remember { mutableStateOf(false) }
    val mensajeSnackbar = remember { mutableStateOf("") }

    val favoritosCargados = FavoritosManager.favoritosCargados
    val billeteras by viewModel.billeteras.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Obtenemos el estado de modo oscuro/claro del ThemeViewModel
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

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

    if (isLoading || !favoritosCargados.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = if (isDarkTheme) Primario else Primario)
        }
        return
    }

    val billeterasOrdenadas = billeteras.sortedWith(
        compareByDescending<Billetera> { billetera ->
            billetera.beneficios.any { it.disponible }
        }.thenBy { it.nombre }
    )

    // Asignamos colores personalizados según el tema
    val fondo = if (isDarkTheme) FondoOscuro else FondoClaro
    val textoPrincipal = if (isDarkTheme) TextoOscuro else TextoClaro
    val textoSecundario = if (isDarkTheme) TextoOscuroSecundario else TextoClaroSecundario
    val primario = Primario

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Mis Billeteras",
                            color = textoPrincipal
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = textoPrincipal
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = fondo
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = fondo
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Explora tus billeteras:",
                style = MaterialTheme.typography.titleMedium,
                color = textoPrincipal
            )
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
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = primario)
            ) {
                Text("Volver al inicio", color = SobrePrimarioClaro)
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
