package com.proyecto.Descuentosya.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

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

private val LightColorScheme = lightColorScheme(
    primary = AzulPrimario,
    secondary = AzulClaro,
    background = Blanco,
    surface = Blanco,
    onPrimary = AzulOscuro,
    onSecondary = AzulOscuro,
    onBackground = AzulOscuro,
    onSurface = AzulOscuro
)

@Composable
fun DescuentosYaTheme(
    darkTheme: Boolean = false, // <- NUEVO
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

