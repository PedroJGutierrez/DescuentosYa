package com.example.descuentosya.ui.screens

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    var message = mutableStateOf("")
    var errorMessage = mutableStateOf<String?>(null)
    var isLoading = mutableStateOf(false)

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun register(email: String, password: String, context: Context, onSuccess: () -> Unit) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
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
                            user?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                                if (verifyTask.isSuccessful) {
                                    message.value = "Correo de verificación enviado. Revisa tu bandeja de entrada."
                                    auth.signOut()  // Cierra sesión para que el usuario verifique antes de iniciar sesión
                                    onSuccess()
                                } else {
                                    errorMessage.value = "Error al enviar correo de verificación: ${verifyTask.exception?.message}"
                                }
                            }
                        } else {
                            errorMessage.value = task.exception?.localizedMessage ?: "Error al registrarse"
                        }
                    }
            }
        } else {
            errorMessage.value = "Por favor, completa todos los campos"
        }
    }
}