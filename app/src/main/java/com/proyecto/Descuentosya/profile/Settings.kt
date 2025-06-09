package com.proyecto.Descuentosya.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.proyecto.DescuentosYa.R
import com.proyecto.Descuentosya.viewmodel.WelcomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val welcomeViewModel: WelcomeViewModel = viewModel()
    val context = LocalContext.current

    // Observar el estado de autenticación
    val isLoggedIn by welcomeViewModel.isLoggedIn.collectAsState()
    val userEmail by welcomeViewModel.currentUserEmail.collectAsState()

    // Verificar estado de autenticación al cargar
    LaunchedEffect(Unit) {
        welcomeViewModel.checkAuthToken(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (isLoggedIn) {
                        IconButton(onClick = {
                            welcomeViewModel.logout(context)
                            navController.navigate("welcome") {
                                popUpTo("welcome") { inclusive = true }
                            }
                        }) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            // Perfil
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.size(100.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.icono),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✎", color = Color.White, fontSize = MaterialTheme.typography.labelSmall.fontSize)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Mostrar el email del usuario si está logueado
                Text(
                    userEmail ?: "Usuario anónimo",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "@${userEmail?.substringBefore('@') ?: "sin_sesion"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                if (isLoggedIn) {
                    Button(
                        onClick = {
                            welcomeViewModel.logout(context)
                            navController.navigate("welcome") {
                                popUpTo("welcome") { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth(0.7f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Cerrar Sesión", color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            val settingsOptions = listOf(
                "Cuenta" to { navController.navigate("account") },
                "Apariencia" to { navController.navigate("appearance") },
                "Suscripciones" to { /* futuro */ },
                "Notificaciones" to { /* futuro */ },
                "Idioma" to { /* futuro */ },
                "Privacidad y seguridad" to { /* futuro */ },
                "Almacenamiento" to { /* futuro */ }
            )

            settingsOptions.forEach { (label, action) ->
                SettingOption(label, onClick = action)
            }
        }
    }
}

@Composable
fun SettingOption(text: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    ) {
        Text(text, style = MaterialTheme.typography.bodyLarge)
        Divider(color = Color.LightGray, thickness = 0.5.dp)
    }
}