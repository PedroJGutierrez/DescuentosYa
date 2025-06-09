package com.example.descuentosya.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.proyecto.Descuentosya.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val loginViewModel: LoginViewModel = viewModel()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iniciar sesión") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }

            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Bienvenido", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                isError = loginViewModel.passwordError.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = MaterialTheme.shapes.medium
            )

            if (loginViewModel.passwordError.value) {
                Text(
                    text = "Contraseña incorrecta",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Button(
                onClick = {
                    loginViewModel.login(email, password, context) {
                        navController.navigate("welcome") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                },
                enabled = !loginViewModel.isLoading.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text(if (loginViewModel.isLoading.value) "Iniciando sesión..." else "Iniciar sesión")
            }

            TextButton(
                onClick = {
                    loginViewModel.resetPassword(email)
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("¿Olvidaste tu contraseña?")
            }

            loginViewModel.errorMessage.value?.let {
                if (!loginViewModel.passwordError.value) {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
                if (loginViewModel.showResendVerification.value) {
                    TextButton(onClick = {
                        loginViewModel.resendVerificationEmail()
                    }) {
                        Text("Reenviar correo de verificación")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(loginViewModel.message.value)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("¿No tienes cuenta? ")
                TextButton(onClick = { navController.navigate("register") }) {
                    Text("Regístrate")
                }
            }
        }
    }
}
