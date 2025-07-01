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
        errorMessage.value = null
        passwordError.value = false

        viewModelScope.launch {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    isLoading.value = false
                    if (task.isSuccessful) {
                        val user = FirebaseAuth.getInstance().currentUser
                        val uid = user?.uid ?: return@addOnCompleteListener

                        if (user.isEmailVerified) {
                            context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                .edit().putString("auth_token", uid).apply()

                            Firebase.firestore.collection("usuarios").document(uid).get()
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
                            FirebaseAuth.getInstance().signOut()
                            errorMessage.value = "Debes verificar tu correo electrónico"
                            showResendVerification.value = true
                        }
                    } else {
                        val exception = task.exception
                        when (exception) {
                            is com.google.firebase.auth.FirebaseAuthInvalidUserException -> {
                                FirebaseAuth.getInstance().signOut()
                                errorMessage.value = "El usuario no existe"
                            }
                            is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> {
                                passwordError.value = true
                                errorMessage.value = "Contraseña incorrecta"
                            }
                            else -> {
                                errorMessage.value = exception?.localizedMessage ?: "Error desconocido al iniciar sesión"
                            }
                        }
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
        user?.reload()?.addOnCompleteListener {
            if (user != null && !user.isEmailVerified) {
                user.sendEmailVerification()
                    .addOnSuccessListener {
                        message.value = "Correo reenviado correctamente"
                        showResendVerification.value = false
                    }
                    .addOnFailureListener {
                        errorMessage.value = "Error al reenviar correo: ${it.message}"
                    }
            } else {
                errorMessage.value = "Este usuario ya verificó su correo o no está disponible"
            }
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
