package com.proyecto.Descuentosya.viewmodel

import android.content.Context
import android.preference.PreferenceManager
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
        _isLoggedIn.value = auth.currentUser != null
        _currentUserEmail.value = auth.currentUser?.email
    }

    fun checkAuthToken(context: Context) {
        viewModelScope.launch {
            val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val savedToken = prefs.getString("auth_token", null)
            val currentUser = auth.currentUser
            if (savedToken != null && currentUser?.uid == savedToken) {
                _isLoggedIn.value = true
                _currentUserEmail.value = currentUser.email
            } else {
                _isLoggedIn.value = false
                _currentUserEmail.value = null
            }
        }
    }

    fun logout(context: Context) {
        auth.signOut()
        _isLoggedIn.value = false
        _currentUserEmail.value = null

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().remove("first_login_shown").apply()

        val sessionPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sessionPrefs.edit().remove("auth_token").apply()
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
