package com.proyecto.Descuentosya.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    var message = mutableStateOf("")
    var errorMessage = mutableStateOf<String?>(null)
    var isLoading = mutableStateOf(false)

    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    fun register(email: String, password: String, context: Context, onSuccess: () -> Unit) {
        if (email.isEmpty() || password.isEmpty()) {
            errorMessage.value = "Por favor, completa todos los campos"
            return
        }

        if (password.length < 6) {
            errorMessage.value = "La contraseña debe tener al menos 6 caracteres"
            return
        }

        isLoading.value = true
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    isLoading.value = false
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val uid = user?.uid ?: return@addOnCompleteListener

                        val userMap = hashMapOf(
                            "email" to email,
                            "tipo" to "Usuario",
                            "nombre" to "",
                            "apellido" to "",
                            "nacionalidad" to "",
                            "telefono" to ""
                        )

                        db.collection("usuarios").document(uid).set(userMap)
                            .addOnSuccessListener {
                                user.sendEmailVerification()
                                    .addOnSuccessListener {
                                        message.value = "Correo de verificación enviado. Revisa tu bandeja de entrada."
                                        auth.signOut()
                                        onSuccess()
                                    }
                                    .addOnFailureListener {
                                        errorMessage.value = "Error al enviar correo de verificación"
                                    }
                            }
                            .addOnFailureListener {
                                errorMessage.value = "Error al guardar usuario en Firestore"
                            }
                    } else {
                        errorMessage.value = task.exception?.message ?: "Error al registrarse"
                    }
                }
        }
    }
}