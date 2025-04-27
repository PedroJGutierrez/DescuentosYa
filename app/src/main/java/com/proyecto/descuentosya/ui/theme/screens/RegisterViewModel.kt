package com.example.descuentosya.ui.screens

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.descuentosya.api.RetrofitClient
import com.example.descuentosya.api.models.RegisterRequest
import com.example.descuentosya.api.models.ApiResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class RegisterViewModel : ViewModel() {

    var message = mutableStateOf("")
    var errorMessage = mutableStateOf<String?>(null)
    var isLoading = mutableStateOf(false)

    fun register(email: String, password: String, context: Context, onSuccess: () -> Unit) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            isLoading.value = true
            errorMessage.value = null
            message.value = ""

            val registerRequest = RegisterRequest(email, password)

            viewModelScope.launch {
                try {
                    val response: Response<ApiResponse> = RetrofitClient.apiService.registerUser(registerRequest)
                    if (response.isSuccessful && response.body() != null) {
                        val apiResponse = response.body()!!
                        message.value = apiResponse.message

                        if (apiResponse.success) {
                            // Guardamos el token en caso de que el backend lo retorne durante el registro
                            apiResponse.token?.let { token ->
                                val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("auth_token", token)
                                editor.apply()
                            }

                            message.value = "Registro completado"
                            onSuccess() // Navegar a login después del registro exitoso
                        } else {
                            errorMessage.value = apiResponse.message
                        }
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                        errorMessage.value = "Error: ${response.code()} - $errorBody"
                    }
                } catch (e: Exception) {
                    errorMessage.value = "Error de red: ${e.localizedMessage}"
                    e.printStackTrace()
                } finally {
                    isLoading.value = false
                }
            }
        } else {
            errorMessage.value = "Por favor, completa todos los campos"
        }
    }
}