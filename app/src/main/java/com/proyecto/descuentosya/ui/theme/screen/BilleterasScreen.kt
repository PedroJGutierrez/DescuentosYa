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
fun BilleterasScreen(navController: NavController) {
    Column(Modifier.padding(16.dp)) {
        Text("Seleccioná tus billeteras", style = MaterialTheme.typography.headlineMedium)

        DataManager.billeteras.forEach { billetera ->
            val isSelected = billetera in DataManager.billeterasSeleccionadas

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = {
                        if (it) DataManager.billeterasSeleccionadas.add(billetera)
                        else DataManager.billeterasSeleccionadas.remove(billetera)
                    }
                )
                Text(billetera, style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Text("Guardar y volver")
        }
    }
}
