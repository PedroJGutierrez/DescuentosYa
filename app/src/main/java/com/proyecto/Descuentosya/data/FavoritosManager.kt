package com.proyecto.Descuentosya.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FavoritosManager {
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    val favoritos = mutableStateListOf<String>()
    var favoritosCargados = mutableStateOf(false)
        private set

    fun cargarFavoritosDesdeFirestore(context: Context) {
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