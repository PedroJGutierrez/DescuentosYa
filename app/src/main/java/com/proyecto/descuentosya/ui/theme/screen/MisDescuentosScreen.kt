package com.proyecto.descuentosya.ui.theme.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.proyecto.descuentosya.auth.AuthManager
import com.proyecto.descuentosya.ui.theme.components.DataManager

@Composable
fun MisDescuentosScreen() {
    Column(Modifier.padding(16.dp)) {
        Text("Mis Billeteras Seleccionadas", style = MaterialTheme.typography.headlineMedium)

        if (DataManager.billeterasSeleccionadas.isEmpty()) {
            Text("No seleccionaste ninguna billetera.")
        } else {
            DataManager.billeterasSeleccionadas.forEach { billetera ->
                Text("- $billetera", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
