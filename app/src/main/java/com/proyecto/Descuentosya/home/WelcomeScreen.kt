package com.proyecto.Descuentosya.home

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.proyecto.Descuentosya.components.Billetera
import com.proyecto.Descuentosya.data.FavoritosManager
import com.proyecto.Descuentosya.viewmodel.ThemeViewModel
import com.proyecto.Descuentosya.viewmodel.WelcomeViewModel
import com.proyecto.Descuentosya.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(navController: NavController) {
    val context = LocalContext.current

    val welcomeViewModel: WelcomeViewModel = viewModel()
    val themeViewModel: ThemeViewModel = viewModel()

    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

    val fondo = if (isDarkTheme) FondoOscuro else FondoClaro
    val textoPrincipal = if (isDarkTheme) TextoOscuro else TextoClaro
    val textoSecundario = if (isDarkTheme) TextoOscuroSecundario else TextoClaroSecundario
    val primario = Primario

    val isLoggedIn by welcomeViewModel.isLoggedIn.collectAsState()
    val userEmail by welcomeViewModel.currentUserEmail.collectAsState()
    val favoritosCargados = FavoritosManager.favoritosCargados.value

    var showWelcomeMessage by remember { mutableStateOf(false) }
    var hasNavigated by remember { mutableStateOf(false) }

    var billeterasFavoritas by remember { mutableStateOf<List<Billetera>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        FavoritosManager.cargarFavoritosDesdeFirestore()
    }

    LaunchedEffect(favoritosCargados) {
        if (favoritosCargados) {
            billeterasFavoritas = FavoritosManager.obtenerBilleterasFavoritas()
            isLoading = false
        }
    }

    LaunchedEffect(isLoggedIn) {
        welcomeViewModel.checkAuthToken(context)

        if (!isLoggedIn && !hasNavigated) {
            hasNavigated = true
            navController.navigate("login") {
                popUpTo("welcome") { inclusive = true }
            }
        }

        if (isLoggedIn && !hasNavigated) {
            hasNavigated = true
            showWelcomeMessage = !welcomeViewModel.hasShownWelcome(context)
            welcomeViewModel.setWelcomeShown(context)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Descuentos Ya",
                        color = textoPrincipal,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    if (isLoggedIn) {
                        IconButton(onClick = { navController.navigate("settings") }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menú",
                                tint = textoPrincipal
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primario),
                modifier = Modifier.shadow(8.dp)
            )
        },
        containerColor = fondo
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (!isLoggedIn) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Inicia sesión para ver tus billeteras favoritas.",
                        color = textoSecundario,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    if (showWelcomeMessage) {
                        Text(
                            "Bienvenido: $userEmail",
                            color = primario,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            "Sesión iniciada correctamente",
                            color = textoSecundario,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    Text(
                        "Tus billeteras favoritas:",
                        style = MaterialTheme.typography.titleLarge,
                        color = textoPrincipal,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = primario)
                            }
                        }

                        billeterasFavoritas.isEmpty() -> {
                            Text(
                                "Todavía no tienes billeteras favoritas.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = textoSecundario,
                                modifier = Modifier.padding(vertical = 40.dp)
                            )
                        }

                        else -> {
                            billeterasFavoritas.forEach { billetera ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(6.dp, RoundedCornerShape(16.dp))
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            brush = if (isDarkTheme) {
                                                Brush.linearGradient(
                                                    colors = listOf(
                                                        SuperficieOscura.copy(alpha = 0.85f),
                                                        SuperficieOscura.copy(alpha = 0.75f)
                                                    )
                                                )
                                            } else {
                                                Brush.linearGradient(
                                                    colors = listOf(
                                                        SobrePrimarioClaro.copy(alpha = 0.95f),
                                                        SobrePrimarioClaro.copy(alpha = 0.85f)
                                                    )
                                                )
                                            }
                                        )
                                ) {
                                    Card(
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.Transparent)
                                    ) {
                                        BannerCard(
                                            billetera = billetera,
                                            navController = navController,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(150.dp),
                                            onFavoritoCambiado = { nombre, _ ->
                                                billeterasFavoritas = billeterasFavoritas.filterNot { it.nombre == nombre }
                                            }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }

                // Botón flotante abajo a la derecha
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    TextButton(
                        onClick = { navController.navigate("billeteras") },
                        colors = ButtonDefaults.textButtonColors(contentColor = primario),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(primario.copy(alpha = 0.1f))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Ver billeteras",
                            style = MaterialTheme.typography.labelLarge,
                            color = primario
                        )
                    }
                }
            }
        }
    }
}

