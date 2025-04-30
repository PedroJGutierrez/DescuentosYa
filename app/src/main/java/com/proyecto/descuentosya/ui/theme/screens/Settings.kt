package com.example.descuentosya.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proyecto.descuentosya.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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
                    // Ícono de editar
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

                Text("Marcelo Gallardo", style = MaterialTheme.typography.titleMedium)
                Text("@admin@correo", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Opciones de configuración
            val settingsOptions = listOf(
                "Cuenta",
                "Apariencia",
                "Suscripciones",
                "Notificaciones",
                "Idioma",
                "Privacidad y seguridad",
                "Almacenamiento"
            )

            settingsOptions.forEach { option ->
                SettingOption(option)
            }
        }
    }
}

@Composable
fun SettingOption(text: String) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { /* acción futura */ }
        .padding(vertical = 12.dp)) {
        Text(text, style = MaterialTheme.typography.bodyLarge)
        Divider(color = Color.LightGray, thickness = 0.5.dp)
    }
}
