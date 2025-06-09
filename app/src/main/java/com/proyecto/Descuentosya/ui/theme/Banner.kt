package com.proyecto.Descuentosya.ui.theme

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.proyecto.DescuentosYa.R
import com.proyecto.Descuentosya.data.FavoritosManager

@Composable
fun BannerCard(
    billetera: String,
    context: Context,
    modifier: Modifier = Modifier,
    onFavoritoCambiado: (String, Boolean) -> Unit = { _, _ -> }
) {
    val esFavorito = FavoritosManager.esFavorito(billetera)

    val iconsWithLabels = listOf(
        Triple(Icons.Default.Fastfood, true, "Comida rápida"),
        Triple(Icons.Default.Movie, true, "Cine"),
        Triple(Icons.Default.ShoppingCart, true, "Supermercados"),
        Triple(Icons.Default.ReceiptLong, true, "Servicios"),
        Triple(Icons.Default.CreditCard, true, "Sube"),
        Triple(Icons.Default.ShowChart, true, "Intereses")
    )

    var tooltipIndex by remember { mutableStateOf<Int?>(null) }
    var tooltipPosition by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current
    val iconPositions = remember { mutableStateMapOf<Int, Offset>() }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Transparent)
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = billetera,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    iconsWithLabels.forEachIndexed { index, (icon, enabled, label) ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .onGloballyPositioned { coordinates ->
                                    // Guardamos la posición de cada icono usando positionInWindow
                                    val position = coordinates.positionInWindow()
                                    iconPositions[index] = Offset(
                                        x = position.x + (coordinates.size.width / 2f), // Centro horizontal del icono
                                        y = position.y // Posición Y del icono
                                    )
                                }
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onLongPress = {
                                            tooltipIndex = index
                                            tooltipPosition = iconPositions[index] ?: Offset.Zero
                                        },
                                        onPress = {
                                            tryAwaitRelease()
                                            tooltipIndex = null
                                        }
                                    )
                                }
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (enabled) Color.White else Color.LightGray,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    if (esFavorito) {
                        FavoritosManager.quitarFavorito(context, billetera)
                    } else {
                        FavoritosManager.agregarFavorito(context, billetera)
                    }
                    onFavoritoCambiado(billetera, !esFavorito)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (esFavorito) "Quitar de Favoritos" else "Agregar a Favoritos")
            }
        }

        // Tooltip posicionado correctamente
        tooltipIndex?.let { index ->
            val label = iconsWithLabels.getOrNull(index)?.third ?: ""
            if (label.isNotEmpty() && tooltipPosition != Offset.Zero) {
                Popup(
                    offset = IntOffset(
                        x = with(density) { (tooltipPosition.x - 60.dp.toPx()).toInt() }, // Centramos el tooltip
                        y = with(density) { (tooltipPosition.y - 60.dp.toPx()).toInt() }  // Posicionamos ARRIBA del icono
                    ),
                    properties = PopupProperties(focusable = false)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Color.Black.copy(alpha = 0.9f),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = label,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}