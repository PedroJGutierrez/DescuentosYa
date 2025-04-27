package com.proyecto.descuentosya.ui.theme.screens

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.descuentosya.auth.AuthManager
import com.example.descuentosya.api.RetrofitClient
import com.example.descuentosya.api.models.ApiResponse
import com.example.descuentosya.api.models.LoginRequest
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel : ViewModel() {

    var message = mutableStateOf("")
    var errorMessage = mutableStateOf<String?>(null)
    var isLoading = mutableStateOf(false)

    // Cambiar a 'true' para probar con el servidor (online) o 'false' para usar AuthManager (offline)
    private val useOnlineLogin = false

    fun login(email: String, password: String, context: Context, onSuccess: () -> Unit) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            isLoading.value = true

            if (useOnlineLogin) {
                // Login online usando Retrofit
                val loginRequest = LoginRequest(email, password)

                viewModelScope.launch {
                    try {
                        val response: Response<ApiResponse> = RetrofitClient.apiService.loginUser(loginRequest)
                        if (response.isSuccessful && response.body() != null) {
                            val apiResponse = response.body()!!
                            message.value = apiResponse.message
                            if (apiResponse.success) {
                                // Guardar el token de autenticación en SharedPreferences
                                val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("auth_token", apiResponse.token) // Guarda el token
                                editor.apply()

                                onSuccess() // Llamar al éxito, por ejemplo, redirigir a la pantalla principal
                            } else {
                                errorMessage.value = apiResponse.message
                            }
                        } else {
                            errorMessage.value = "Error: ${response.code()} - ${response.message()}"
                        }
                    } catch (e: Exception) {
                        errorMessage.value = "Error de red: ${e.localizedMessage}"
                        e.printStackTrace() // Imprime el error completo para debugging
                    } finally {
                        isLoading.value = false
                    }
                }
            } else {
                // Login offline usando AuthManager
                viewModelScope.launch {
                    try {
                        if (AuthManager.login(email, password)) {
                            // Guardar token de autenticación en SharedPreferences (igual que login online)
                            val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("auth_token", "offline_token") // Puede ser cualquier string, por ejemplo "offline_token"
                            editor.apply()

                            message.value = "Login exitoso!"
                            onSuccess()

                        } else {
                            errorMessage.value = "Credenciales inválidas"
                        }
                    } catch (e: Exception) {
                        errorMessage.value = "Error: ${e.localizedMessage}"
                        e.printStackTrace()
                    } finally {
                        isLoading.value = false
                    }
                }
            }
        } else {
            errorMessage.value = "Por favor, completa todos los campos"
        }
    }
}
