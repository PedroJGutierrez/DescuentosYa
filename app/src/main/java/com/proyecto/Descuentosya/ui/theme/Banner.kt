package com.proyecto.Descuentosya.ui.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.proyecto.DescuentosYa.R
import com.proyecto.Descuentosya.components.Billetera
import com.proyecto.Descuentosya.components.IconMapper
import com.proyecto.Descuentosya.data.FavoritosManager

@Composable
fun getBackgroundForWallet(nombre: String): Int {
    return when (nombre.lowercase()) {
        "mercado pago"   -> R.drawable.mercado
        "bbva"           -> R.drawable.bbva
        "banco nación"   -> R.drawable.nacion
        "banco provincia"-> R.drawable.provincia
        "banco ciudad"   -> R.drawable.ciudad
        "banco galicia"  -> R.drawable.galicia
        "banco santander"-> R.drawable.santander
        "banco macro"    -> R.drawable.macro
        "banco hsbc"     -> R.drawable.hsbc
        else             -> R.drawable.banner
    }
}

@Composable
fun BannerCard(
    billetera: Billetera,
    navController: NavController,
    modifier: Modifier = Modifier,
    onFavoritoCambiado: (String, Boolean) -> Unit = { _, _ -> }
) {
    val esFavorito by remember {
        derivedStateOf { FavoritosManager.esFavorito(billetera.nombre) }
    }

    var isPressed by remember { mutableStateOf(false) }
    val backgroundResId = getBackgroundForWallet(billetera.nombre)

    // Animación suave de opacidad para la capa semitransparente
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.2f else 0.5f,
        label = "alphaAnim"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    // Press prolongado — solo cambia el sombreado
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()   // Espera suelte o cancele
                        isPressed = false   // Al soltar/cancelar vuelve
                    },
                    // Tap corto — navega al detalle
                    onTap = {
                        navController.navigate("billetera_detalle/${billetera.nombre}")
                    }
                )
            }
    ) {
        /* Fondo */
        Image(
            painter = painterResource(id = backgroundResId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        /* Capa oscura animada */
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = animatedAlpha))
        )

        /* Contenido */
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
                            FavoritosManager.toggleFavorito(billetera.nombre)
                            onFavoritoCambiado(billetera.nombre, !esFavorito)
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .padding(0.dp)
                    ) {
                        Text(
                            text = if (esFavorito) "-" else "+",
                            color = Color(0xFF448AFF),
                            fontSize = 24.sp
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
                    Box(
                        modifier = Modifier.size(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = IconMapper.getIconByName(beneficio.iconName),
                            contentDescription = beneficio.descripcion,
                            tint = if (beneficio.disponible) Color.White else Color.Gray, // APLICAR DIRECTAMENTE AQUÍ
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}