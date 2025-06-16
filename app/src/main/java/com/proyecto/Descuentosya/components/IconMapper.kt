package com.proyecto.Descuentosya.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

object IconMapper {
    private val iconMap = mapOf(
        "Fastfood" to Icons.Default.Fastfood,
        "Movie" to Icons.Default.Movie,
        "ShoppingCart" to Icons.Default.ShoppingCart,
        "ReceiptLong" to Icons.Default.ReceiptLong,
        "CreditCard" to Icons.Default.CreditCard,
        "ShowChart" to Icons.Default.ShowChart
    )

    private val categoryMap = mapOf(
        "Fastfood" to "Comida rápida",
        "Movie" to "Cine",
        "ShoppingCart" to "Supermercados",
        "ReceiptLong" to "Servicios",
        "CreditCard" to "Sube",
        "ShowChart" to "Rendimientos"
    )

    fun getIconByName(name: String): ImageVector {
        val cleanName = name.removePrefix("Filled.").trim()
        return iconMap[cleanName] ?: Icons.Default.Help
    }

    fun getCategoryByIconName(iconName: String): String {
        val cleanName = iconName.removePrefix("Filled.").trim()
        return categoryMap[cleanName] ?: "Otros"
    }

    fun generateDescriptionForIcon(iconName: String, hasDiscount: Boolean): String {
        val cleanName = iconName.removePrefix("Filled.").trim()
        return if (cleanName == "ShowChart") {
            val porcentaje = (5..30).random()
            "$porcentaje% de rendimientos anuales con esta billetera."
        } else {
            val category = getCategoryByIconName(iconName)
            if (hasDiscount) {
                val porcentaje = listOf(5, 10, 15, 20, 25, 30, 40).random()
                "Hasta $porcentaje% de descuento en $category"
            } else {
                "Sin descuentos en $category"
            }
        }
    }

    val availableIcons = listOf(
        "Fastfood", "Movie", "ShoppingCart", "ReceiptLong", "CreditCard", "ShowChart"
    )
}
