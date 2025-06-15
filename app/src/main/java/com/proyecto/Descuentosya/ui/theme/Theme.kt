package com.proyecto.Descuentosya.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = GrisTexto,            // botones principales
    secondary = GrisTexto,          // elementos secundarios
    background = GrisOscuro,        // fondo de la app
    surface = GrisIntermedio,       // tarjetas / sheets
    onPrimary = Blanco,             // texto sobre botones
    onSecondary = Blanco,           // texto sobre elementos secundarios
    onBackground = Blanco,          // texto general sobre fondo
    onSurface = Blanco              // texto sobre tarjetas
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
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
