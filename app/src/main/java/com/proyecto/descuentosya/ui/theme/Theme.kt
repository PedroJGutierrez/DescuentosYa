package com.proyecto.descuentosya.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme

private val DarkColorScheme = darkColorScheme(
    primary = AzulPrimario,
    secondary = AzulClaro,
    background = AzulOscuro,
    surface = AzulOscuro,
    onPrimary = Blanco,
    onSecondary = Blanco,
    onBackground = Blanco,
    onSurface = Blanco
)

@Composable
fun DescuentosYaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(),
        content = content
    )
}
