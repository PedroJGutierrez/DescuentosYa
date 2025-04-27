package com.proyecto.descuentosya.data

object FavoritosManager {
    private val _favoritos = mutableSetOf<String>()
    val favoritos: Set<String> get() = _favoritos

    fun agregarFavorito(billetera: String) {
        _favoritos.add(billetera)
    }

    fun eliminarFavorito(billetera: String) {
        _favoritos.remove(billetera)
    }

    fun limpiarFavoritos() {
        _favoritos.clear()
    }
}
