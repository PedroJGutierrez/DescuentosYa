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
import androidx.navigation.NavController
import com.proyecto.Descuentosya.data.FavoritosManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisDescuentosScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val authToken = sharedPreferences.getString("auth_token", null)

    if (authToken == null) {
        LaunchedEffect(Unit) {
            navController.navigate("login") {
                popUpTo("billeteras_favoritas") { inclusive = true }
            }
        }
        return
    }

    val billeterasFavoritas = FavoritosManager.favoritos.toList()

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
        }
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
                            Column(modifier = Modifier.padding(16.dp)) {
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
    }
}
