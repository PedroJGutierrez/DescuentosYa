package com.proyecto.Descuentosya.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class Beneficio(
    val icon: ImageVector,
    val disponible: Boolean,
    val descripcion: String
)

data class Billetera(
    val nombre: String,
    val beneficios: List<Beneficio>
)

object DataManager {
    val billeteras = listOf(
        Billetera(
            nombre = "Mercado Pago",
            beneficios = listOf(
                Beneficio(Icons.Default.Fastfood, true, "Comida rápida"),
                Beneficio(Icons.Default.Movie, true, "Cine"),
                Beneficio(Icons.Default.ShoppingCart, true, "Supermercados"),
                Beneficio(Icons.Default.ReceiptLong, false, "Servicios"),
                Beneficio(Icons.Default.CreditCard, true, "Sube"),
                Beneficio(Icons.Default.ShowChart, false, "Intereses")
            )
        ),
        Billetera(
            nombre = "Ualá",
            beneficios = listOf(
                Beneficio(Icons.Default.Fastfood, true, "Comida rápida"),
                Beneficio(Icons.Default.Movie, false, "Cine"),
                Beneficio(Icons.Default.ShoppingCart, true, "Supermercados"),
                Beneficio(Icons.Default.ReceiptLong, true, "Servicios"),
                Beneficio(Icons.Default.CreditCard, false, "Sube"),
                Beneficio(Icons.Default.ShowChart, true, "Intereses")
            )
        ),
        Billetera(
            nombre = "BBVA",
            beneficios = listOf(
                Beneficio(Icons.Default.Fastfood, false, "Comida rápida"),
                Beneficio(Icons.Default.Movie, true, "Cine"),
                Beneficio(Icons.Default.ShoppingCart, false, "Supermercados"),
                Beneficio(Icons.Default.ReceiptLong, true, "Servicios"),
                Beneficio(Icons.Default.CreditCard, true, "Sube"),
                Beneficio(Icons.Default.ShowChart, true, "Intereses")
            )
        ),
        Billetera(
            nombre = "Banco Nación",
            beneficios = listOf(
                Beneficio(Icons.Default.Fastfood, true, "Comida rápida"),
                Beneficio(Icons.Default.Movie, false, "Cine"),
                Beneficio(Icons.Default.ShoppingCart, true, "Supermercados"),
                Beneficio(Icons.Default.ReceiptLong, true, "Servicios"),
                Beneficio(Icons.Default.CreditCard, true, "Sube"),
                Beneficio(Icons.Default.ShowChart, false, "Intereses")
            )
        ),
        Billetera(
            nombre = "Banco Provincia",
            beneficios = listOf(
                Beneficio(Icons.Default.Fastfood, false, "Comida rápida"),
                Beneficio(Icons.Default.Movie, true, "Cine"),
                Beneficio(Icons.Default.ShoppingCart, true, "Supermercados"),
                Beneficio(Icons.Default.ReceiptLong, true, "Servicios"),
                Beneficio(Icons.Default.CreditCard, true, "Sube"),
                Beneficio(Icons.Default.ShowChart, true, "Intereses")
            )
        ),
        Billetera(
            nombre = "Banco Ciudad",
            beneficios = listOf(
                Beneficio(Icons.Default.Fastfood, true, "Comida rápida"),
                Beneficio(Icons.Default.Movie, true, "Cine"),
                Beneficio(Icons.Default.ShoppingCart, false, "Supermercados"),
                Beneficio(Icons.Default.ReceiptLong, false, "Servicios"),
                Beneficio(Icons.Default.CreditCard, true, "Sube"),
                Beneficio(Icons.Default.ShowChart, true, "Intereses")
            )
        ),
        Billetera(
            nombre = "Banco Galicia",
            beneficios = listOf(
                Beneficio(Icons.Default.Fastfood, true, "Comida rápida"),
                Beneficio(Icons.Default.Movie, false, "Cine"),
                Beneficio(Icons.Default.ShoppingCart, true, "Supermercados"),
                Beneficio(Icons.Default.ReceiptLong, true, "Servicios"),
                Beneficio(Icons.Default.CreditCard, true, "Sube"),
                Beneficio(Icons.Default.ShowChart, false, "Intereses")
            )
        ),
        Billetera(
            nombre = "Banco Santander",
            beneficios = listOf(
                Beneficio(Icons.Default.Fastfood, false, "Comida rápida"),
                Beneficio(Icons.Default.Movie, false, "Cine"),
                Beneficio(Icons.Default.ShoppingCart, true, "Supermercados"),
                Beneficio(Icons.Default.ReceiptLong, true, "Servicios"),
                Beneficio(Icons.Default.CreditCard, false, "Sube"),
                Beneficio(Icons.Default.ShowChart, true, "Intereses")
            )
        ),
        Billetera(
            nombre = "Banco Macro",
            beneficios = listOf(
                Beneficio(Icons.Default.Fastfood, true, "Comida rápida"),
                Beneficio(Icons.Default.Movie, true, "Cine"),
                Beneficio(Icons.Default.ShoppingCart, true, "Supermercados"),
                Beneficio(Icons.Default.ReceiptLong, true, "Servicios"),
                Beneficio(Icons.Default.CreditCard, true, "Sube"),
                Beneficio(Icons.Default.ShowChart, true, "Intereses")
            )
        ),
        Billetera(
            nombre = "Banco HSBC",
            beneficios = listOf(
                Beneficio(Icons.Default.Fastfood, false, "Comida rápida"),
                Beneficio(Icons.Default.Movie, true, "Cine"),
                Beneficio(Icons.Default.ShoppingCart, false, "Supermercados"),
                Beneficio(Icons.Default.ReceiptLong, false, "Servicios"),
                Beneficio(Icons.Default.CreditCard, true, "Sube"),
                Beneficio(Icons.Default.ShowChart, false, "Intereses")
            )
        )
    )
}
