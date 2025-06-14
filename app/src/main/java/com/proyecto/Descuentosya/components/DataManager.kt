package com.proyecto.Descuentosya.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

// Data class
data class Beneficio(
    val icon: ImageVector,
    val disponible: Boolean,
    val descripcion: String,
    val iconName: String
)

data class Billetera(
    val nombre: String,
    val beneficios: List<Beneficio>
)

object DataManager {

    private fun generarBeneficiosAleatorios(): List<Beneficio> {
        return IconMapper.availableIcons.map { iconName ->
            val tieneDescuento = (0..100).random() > 30 // 70% chance
            val categoria = IconMapper.getCategoryByIconName(iconName)
            val descripcion = if (tieneDescuento)
                "Hasta 20% de descuento en $categoria"
            else
                "Sin descuentos en $categoria"

            Beneficio(
                icon = IconMapper.getIconByName(iconName),
                disponible = tieneDescuento,
                descripcion = descripcion,
                iconName = iconName
            )
        }
    }

    private val nombresBilleteras = listOf(
        "Mercado Pago", "Ualá", "BBVA", "Banco Nación", "Banco Provincia",
        "Banco Ciudad", "Banco Galicia", "Banco Santander", "Banco Macro", "Banco HSBC"
    )

    val billeteras: List<Billetera> = nombresBilleteras.map { nombre ->
        Billetera(nombre = nombre, beneficios = generarBeneficiosAleatorios())
    }
}