package com.proyecto.Descuentosya.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import android.content.Context

private val Context.dataStore by preferencesDataStore("settings")

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val themeKey = booleanPreferencesKey("dark_theme")

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    init {
        // Leer el valor almacenado en DataStore
        viewModelScope.launch {
            application.dataStore.data
                .map { it[themeKey] ?: false }
                .collect { _isDarkTheme.value = it }
        }
    }

    fun setDarkTheme(enabled: Boolean) {
        _isDarkTheme.value = enabled
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { prefs ->
                prefs[themeKey] = enabled
            }
        }
    }
}

