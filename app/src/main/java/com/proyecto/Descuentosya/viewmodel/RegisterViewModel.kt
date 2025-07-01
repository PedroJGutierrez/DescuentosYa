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
        errorMessage.value = null
        message.value = ""

        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user == null) {
                            isLoading.value = false
                            errorMessage.value = "Error inesperado: usuario no encontrado"
                            return@addOnCompleteListener
                        }

                        val uid = user.uid

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
                                        isLoading.value = false
                                        message.value = "Correo de verificación enviado. Revisa tu email."
                                        errorMessage.value = null
                                        onSuccess()
                                    }
                                    .addOnFailureListener {
                                        isLoading.value = false
                                        errorMessage.value = "Error al enviar correo de verificación: ${it.message}"
                                    }
                            }
                            .addOnFailureListener {
                                isLoading.value = false
                                errorMessage.value = "Error al guardar usuario en Firestore: ${it.message}"
                            }
                    } else {
                        isLoading.value = false
                        val exception = task.exception
                        when {
                            exception?.message?.contains("email address is already in use", true) == true -> {
                                errorMessage.value = "Este correo ya está registrado"
                            }
                            exception?.message?.contains("email address is badly formatted", true) == true -> {
                                errorMessage.value = "El correo ingresado no es válido"
                            }
                            else -> {
                                errorMessage.value = exception?.localizedMessage ?: "Error al registrarse"
                            }
                        }
                    }
                }
        }
    }
}
