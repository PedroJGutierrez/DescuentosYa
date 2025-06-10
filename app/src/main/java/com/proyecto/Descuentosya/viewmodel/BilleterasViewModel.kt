package com.proyecto.Descuentosya.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.Descuentosya.components.Billetera
import com.proyecto.Descuentosya.data.BilleterasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BilleterasViewModel : ViewModel() {

    private val _billeteras = MutableStateFlow<List<Billetera>>(emptyList())
    val billeteras: StateFlow<List<Billetera>> get() = _billeteras

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    init {
        cargarBilleteras()
    }

    private fun cargarBilleteras() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _billeteras.value = BilleterasRepository.obtenerBilleteras()
            } catch (e: Exception) {
                _billeteras.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
