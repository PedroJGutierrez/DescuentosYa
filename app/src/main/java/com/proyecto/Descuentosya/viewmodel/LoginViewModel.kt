package com.proyecto.Descuentosya.viewmodel
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var message = mutableStateOf("")
    var errorMessage = mutableStateOf<String?>(null)
    var isLoading = mutableStateOf(false)
    var showResendVerification = mutableStateOf(false)
    var passwordError = mutableStateOf(false)
    var userType = mutableStateOf<String?>(null)

    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    fun login(email: String, password: String, context: Context, onSuccess: () -> Unit) {
        if (email.isEmpty() || password.isEmpty()) {
            errorMessage.value = "Por favor, completa todos los campos"
            return
        }

        isLoading.value = true
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    isLoading.value = false
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val uid = user?.uid ?: return@addOnCompleteListener

                        if (user.isEmailVerified) {
                            // Guardar sesión
                            context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                .edit().putString("auth_token", uid).apply()

                            db.collection("usuarios").document(uid).get()
                                .addOnSuccessListener { doc ->
                                    if (doc.exists()) {
                                        userType.value = doc.getString("tipo") ?: "Usuario"
                                    }
                                    message.value = "Inicio de sesión exitoso"
                                    onSuccess()
                                }
                                .addOnFailureListener {
                                    errorMessage.value = "Error al obtener datos del usuario"
                                }

                        } else {
                            auth.signOut()
                            errorMessage.value = "Debes verificar tu correo"
                            showResendVerification.value = true
                        }
                    } else {
                        val errorMsg = task.exception?.message ?: "Error desconocido"
                        passwordError.value = errorMsg.contains("password", ignoreCase = true)
                        errorMessage.value = if (passwordError.value) "Contraseña incorrecta" else errorMsg
                    }
                }
        }
    }

    fun resetPassword(email: String) {
        if (email.isNotEmpty()) {
            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    message.value = "Correo para restablecer contraseña enviado"
                }
                .addOnFailureListener {
                    errorMessage.value = "Error al enviar correo: ${it.message}"
                }
        } else {
            errorMessage.value = "Por favor, ingresa tu correo"
        }
    }

    fun resendVerificationEmail() {
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnSuccessListener {
                message.value = "Correo reenviado"
            }
            ?.addOnFailureListener {
                errorMessage.value = "Error: ${it.message}"
            }
    }
}
