package com.proyecto.Descuentosya.ui.theme

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.proyecto.DescuentosYa.R
import com.proyecto.Descuentosya.components.Billetera
import com.proyecto.Descuentosya.data.FavoritosManager

@Composable
fun BannerCard(
    billetera: Billetera,
    context: Context,
    modifier: Modifier = Modifier,
    onFavoritoCambiado: (String, Boolean) -> Unit = { _, _ -> }
) {
    var esFavorito by remember { mutableStateOf(FavoritosManager.esFavorito(billetera.nombre)) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Transparent)
            .clickable { /* por ahora no hace nada */ }
    ) {
        Image(
            painter = painterResource(id = R.drawable.banner),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = billetera.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.weight(1f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (esFavorito) {
                                FavoritosManager.quitarFavorito(context, billetera.nombre)
                            } else {
                                FavoritosManager.agregarFavorito(context, billetera.nombre)
                            }
                            esFavorito = !esFavorito
                            onFavoritoCambiado(billetera.nombre, esFavorito)
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar",
                            tint = Color(0xFF448AFF) // Azul claro
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = Color(0xFF448AFF),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                billetera.beneficios.forEach { beneficio ->
                    Box(modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = beneficio.icon,
                            contentDescription = null,
                            tint = if (beneficio.disponible) Color.White else Color.LightGray,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}
