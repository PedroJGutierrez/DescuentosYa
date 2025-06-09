package com.proyecto.Descuentosya.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.zIndex
import com.proyecto.DescuentosYa.R

@Composable
fun FondoCelesteBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.fondoceleste),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .zIndex(-1f) // 🔑 Esta línea asegura que la imagen quede detrás de todo
        )
        content()
    }
}
