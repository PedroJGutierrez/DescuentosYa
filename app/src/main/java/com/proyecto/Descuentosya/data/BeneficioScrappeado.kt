package com.proyecto.Descuentosya.data

data class BeneficioScrappeado(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val conditions: String = "",
    val days: List<String> = emptyList(),
    val image: String = "",
    val category: String = ""
)

