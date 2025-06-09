package com.proyecto.Descuentosya.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.preference.PreferenceManager

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

        // Limpiar shared preference de bienvenida
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().remove("first_login_shown").apply()
    }

    fun hasShownWelcome(context: Context): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getBoolean("first_login_shown", false)
    }

    fun setWelcomeShown(context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().putBoolean("first_login_shown", true).apply()
    }
}
