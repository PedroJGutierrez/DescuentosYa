package com.example.descuentosya.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WelcomeViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> get() = _isLoggedIn

    private val _currentUserEmail = MutableStateFlow<String?>(null)
    val currentUserEmail: StateFlow<String?> get() = _currentUserEmail

    init {
        checkAuthState()
    }

    fun checkAuthState() {
        val user = auth.currentUser
        _isLoggedIn.value = user != null
        _currentUserEmail.value = user?.email
    }

    fun checkAuthToken(context: Context) {
        viewModelScope.launch {
            val user = auth.currentUser
            _isLoggedIn.value = user != null
            _currentUserEmail.value = user?.email
        }
    }

    fun logout(context: Context) {
        auth.signOut()
        _isLoggedIn.value = false
        _currentUserEmail.value = null

        // Limpiar SharedPreferences si es necesario
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("auth_token")
        editor.apply()
    }
}