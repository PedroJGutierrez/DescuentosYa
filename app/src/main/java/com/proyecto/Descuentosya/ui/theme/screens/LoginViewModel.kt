package com.proyecto.Descuentosya.ui.theme.screens

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var message = mutableStateOf("")
    var errorMessage = mutableStateOf<String?>(null)
    var isLoading = mutableStateOf(false)
    var showResendVerification = mutableStateOf(false)

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(email: String, password: String, context: Context, onSuccess: () -> Unit) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            isLoading.value = true
            errorMessage.value = null
            message.value = ""

            viewModelScope.launch {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        isLoading.value = false
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            if (user != null && user.isEmailVerified) {
                                // Guardar token en SharedPreferences
                                val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("auth_token", user.uid)
                                editor.apply()
                                showResendVerification.value = false
                                message.value = "Inicio de sesión exitoso"
                                onSuccess()
                            } else {
                                auth.signOut() // Importante: cerrar sesión si no está verificado
                                errorMessage.value = "Debes verificar tu correo antes de iniciar sesión"
                                showResendVerification.value = true
                            }

                        }
                    }
            }
        } else {
            errorMessage.value = "Por favor, completa todos los campos"
        }
    }

    fun resetPassword(email: String) {
        if (email.isNotEmpty()) {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        message.value = "Se envió un correo para restablecer la contraseña"
                        errorMessage.value = null
                    } else {
                        errorMessage.value = "No se pudo enviar el correo de recuperación"
                    }
                }
        } else {
            errorMessage.value = "Ingresa tu correo para restablecer la contraseña"
        }
    }

    // Usuario admin por defecto para pruebas (solo para debugging)
    fun loginAsAdmin(context: Context, onSuccess: () -> Unit) {
        val adminEmail = "admin@test.com"
        val adminPassword = "admin123"

        login(adminEmail, adminPassword, context, onSuccess)
    }

    fun resendVerificationEmail() {
        val user = auth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                message.value = "Correo de verificación reenviado"
            } else {
                errorMessage.value = "No se pudo reenviar el correo"
            }
        }
    }
}
