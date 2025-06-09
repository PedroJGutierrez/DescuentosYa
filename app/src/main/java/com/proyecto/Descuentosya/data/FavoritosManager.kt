package com.proyecto.Descuentosya.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FavoritosManager {
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    // Lista reactiva que funciona con Jetpack Compose
    val favoritos = mutableStateListOf<String>()

    fun cargarFavoritosDesdeFirestore(context: Context) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { doc ->
                val lista = doc.get("favoritos") as? List<String>
                favoritos.clear()
                if (lista != null) {
                    favoritos.addAll(lista)
                }
            }
            .addOnFailureListener {
                // Podés loguear o manejar el error si querés
            }
    }

    fun agregarFavorito(context: Context, billetera: String) {
        val uid = auth.currentUser?.uid ?: return
        val docRef = db.collection("usuarios").document(uid)

        docRef.update("favoritos", FieldValue.arrayUnion(billetera))
            .addOnSuccessListener {
                if (!favoritos.contains(billetera)) {
                    favoritos.add(billetera)
                }
            }
    }

    fun quitarFavorito(context: Context, billetera: String) {
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
}