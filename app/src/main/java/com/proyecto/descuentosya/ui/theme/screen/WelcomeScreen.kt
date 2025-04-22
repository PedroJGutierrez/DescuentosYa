package com.proyecto.descuentosya.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proyecto.descuentosya.R

data class Oferta(val nombre: String, val descripcion: String, val imagenResId: Int)

val ofertasEjemplo = listOf(
    Oferta("MercadoPago", "20% de descuento en supermercados", R.drawable.mercadopago),
    Oferta("Ualá", "15% en farmacias los lunes", R.drawable.uala),
    Oferta("Modo", "10% en restaurantes adheridos", R.drawable.modo),
    Oferta("Naranja X", "Cuotas sin interés en tecnología", R.drawable.naranjax)
)

@Composable
fun WelcomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Visualización centralizada de descuentos activos",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(ofertasEjemplo) { oferta ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = oferta.imagenResId),
                            contentDescription = oferta.nombre,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(64.dp)
                                .padding(end = 16.dp)
                        )
                        Column {
                            Text(oferta.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(oferta.descripcion, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Iniciar Sesión")
        }

        OutlinedButton(
            onClick = { navController.navigate("register") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Registrarse")
        }
    }
}

