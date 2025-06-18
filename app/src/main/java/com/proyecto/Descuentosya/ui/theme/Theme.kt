package com.proyecto.Descuentosya.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

val EsquemaClaro = lightColorScheme(
    primary = Primario,
    secondary = Secundario,
    background = FondoClaro,
    surface = SuperficieClara,
    onPrimary = SobrePrimarioClaro,
    onBackground = TextoClaro,
    onSurface = TextoClaro,
    error = Error
)

val EsquemaOscuro = darkColorScheme(
    primary = Primario,
    secondary = Secundario,
    background = FondoOscuro,
    surface = SuperficieOscura,
    onPrimary = SobrePrimarioOscuro,
    onBackground = TextoOscuro,
    onSurface = TextoOscuro,
    error = Error
)

@Composable
fun DescuentosYaTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
