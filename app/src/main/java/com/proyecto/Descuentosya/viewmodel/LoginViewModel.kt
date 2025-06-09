package com.proyecto.Descuentosya.viewmodel

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

    var passwordError = mutableStateOf(false)

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(email: String, password: String, context: Context, onSuccess: () -> Unit) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            isLoading.value = true
            errorMessage.value = null
            message.value = ""
            passwordError.value = false

            viewModelScope.launch {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        isLoading.value = false
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            if (user != null && user.isEmailVerified) {
                                val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("auth_token", user.uid)
                                editor.apply()
                                showResendVerification.value = false
                                message.value = "Inicio de sesión exitoso"
                                onSuccess()
                            } else {
                                auth.signOut()
                                errorMessage.value = "Debes verificar tu correo antes de iniciar sesión"
                                showResendVerification.value = true
                            }
                        } else {
                            val errorMsg = task.exception?.message ?: "Error desconocido"
                            errorMessage.value = when {
                                errorMsg.contains("password is invalid", ignoreCase = true) ||
                                        errorMsg.contains("The password is invalid", ignoreCase = true) ||
                                        errorMsg.contains("INVALID_PASSWORD", ignoreCase = true) -> {
                                    passwordError.value = true
                                    "Contraseña incorrecta"
                                }
                                else -> errorMsg
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
