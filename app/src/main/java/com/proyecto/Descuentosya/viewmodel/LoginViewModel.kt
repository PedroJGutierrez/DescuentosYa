package com.proyecto.Descuentosya.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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

    fun handleGoogleSignIn(account: GoogleSignInAccount, context: Context, onSuccess: () -> Unit) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid ?: return@addOnCompleteListener

                    context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        .edit().putString("auth_token", uid).apply()

                    val userDocRef = db.collection("usuarios").document(uid)
                    userDocRef.get().addOnSuccessListener { doc ->
                        val nombreCompleto = account.displayName?.split(" ") ?: listOf("")
                        val nombre = nombreCompleto.getOrNull(0) ?: ""
                        val apellido = nombreCompleto.drop(1).joinToString(" ")
                        val email = account.email ?: ""
                        val photoUrl = account.photoUrl?.toString() ?: ""

                        if (!doc.exists()) {
                            val userMap = hashMapOf(
                                "email" to email,
                                "tipo" to "Usuario",
                                "nombre" to nombre,
                                "apellido" to apellido,
                                "nacionalidad" to "",
                                "telefono" to "",
                                "gmail" to email,
                                "foto_google" to photoUrl
                            )
                            userDocRef.set(userMap)
                                .addOnSuccessListener { onSuccess() }
                                .addOnFailureListener {
                                    errorMessage.value = "Error al guardar usuario en Firestore"
                                }
                        } else {
                            val updates = mapOf(
                                "gmail" to email,
                                "foto_google" to photoUrl
                            )
                            userDocRef.update(updates)
                                .addOnSuccessListener { onSuccess() }
                                .addOnFailureListener {
                                    errorMessage.value = "Error al actualizar datos de Google"
                                }
                        }
                    }
                } else {
                    errorMessage.value = "Error al autenticar con Google"
                }
            }
    }
}
