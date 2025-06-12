package com.proyecto.Descuentosya.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object FavoritosManager {
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    val favoritos = mutableStateListOf<String>()
    var favoritosCargados = mutableStateOf(false)
        private set

    fun cargarFavoritosDesdeFirestore() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { doc ->
                val lista = doc.get("favoritos") as? List<String>
                favoritos.clear()
                if (lista != null) {
                    favoritos.addAll(lista)
                }
                favoritosCargados.value = true
            }
            .addOnFailureListener {
                favoritosCargados.value = true
            }
    }

    // Versión suspendida para uso con corrutinas
    suspend fun cargarFavoritosDesdeFirestoreSuspend(): List<String> {
        val uid = auth.currentUser?.uid ?: return emptyList()

        return try {
            val doc = db.collection("usuarios").document(uid).get().await()
            val lista = doc.get("favoritos") as? List<String> ?: emptyList()
            favoritos.clear()
            favoritos.addAll(lista)
            favoritosCargados.value = true
            lista
        } catch (e: Exception) {
            favoritosCargados.value = true
            emptyList()
        }
    }

    fun agregarFavorito(billetera: String) {
        val uid = auth.currentUser?.uid ?: return
        val docRef = db.collection("usuarios").document(uid)

        // Primero verificar si el documento existe, si no, crearlo
        docRef.get().addOnSuccessListener { doc ->
            if (!doc.exists()) {
                // Crear documento con favoritos vacío
                docRef.set(mapOf("favoritos" to listOf<String>()))
                    .addOnSuccessListener {
                        // Después agregar el favorito
                        agregarFavoritoInterno(docRef, billetera)
                    }
            } else {
                agregarFavoritoInterno(docRef, billetera)
            }
        }
    }

    private fun agregarFavoritoInterno(docRef: com.google.firebase.firestore.DocumentReference, billetera: String) {
        docRef.update("favoritos", FieldValue.arrayUnion(billetera))
            .addOnSuccessListener {
                if (!favoritos.contains(billetera)) {
                    favoritos.add(billetera)
                }
            }
            .addOnFailureListener { e ->
                // Si falla el update, intentar crear el campo
                docRef.set(mapOf("favoritos" to listOf(billetera)), com.google.firebase.firestore.SetOptions.merge())
                    .addOnSuccessListener {
                        if (!favoritos.contains(billetera)) {
                            favoritos.add(billetera)
                        }
                    }
            }
    }

    fun quitarFavorito(billetera: String) {
        val uid = auth.currentUser?.uid ?: return
        val docRef = db.collection("usuarios").document(uid)

        docRef.update("favoritos", FieldValue.arrayRemove(billetera))
            .addOnSuccessListener {
                favoritos.remove(billetera)
            }
    }

    fun esFavorito(billetera: String): Boolean {
        return favoritos.contains(billetera)
    }

    // Función para obtener billeteras favoritas con sus datos completos
    suspend fun obtenerBilleterasFavoritas(): List<com.proyecto.Descuentosya.components.Billetera> {
        if (favoritos.isEmpty()) return emptyList()

        return try {
            val todasLasBilleteras = BilleterasRepository.obtenerBilleteras()
            todasLasBilleteras.filter { billetera ->
                favoritos.contains(billetera.nombre)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Función para limpiar favoritos (útil para logout)
    fun limpiarFavoritos() {
        favoritos.clear()
        favoritosCargados.value = false
    }

    // Función para sincronizar favoritos después de cambios en los datos
    fun sincronizarConDatos() {
        val uid = auth.currentUser?.uid ?: return
        cargarFavoritosDesdeFirestore()
    }

    // Función para toggle favorito (agregar o quitar)
    fun toggleFavorito(billetera: String) {
        if (esFavorito(billetera)) {
            quitarFavorito(billetera)
        } else {
            agregarFavorito(billetera)
        }
    }

    // Función para inicializar con usuario autenticado
    fun inicializarConUsuario() {
        if (auth.currentUser != null) {
            cargarFavoritosDesdeFirestore()
        } else {
            limpiarFavoritos()
        }
    }
}