package com.proyecto.Descuentosya.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.proyecto.Descuentosya.ui.theme.Primario
import com.proyecto.Descuentosya.ui.theme.SobrePrimarioClaro
import com.proyecto.Descuentosya.ui.theme.TextoClaro

import com.proyecto.Descuentosya.viewmodel.RegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    val registerViewModel: RegisterViewModel = viewModel()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrarse", color = SobrePrimarioClaro) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = SobrePrimarioClaro)
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Primario
                )
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
            Text(
                "Crea tu cuenta",
                style = MaterialTheme.typography.headlineMedium,
                color = TextoClaro
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primario,
                    unfocusedBorderColor = TextoClaro.copy(alpha = 0.3f),
                    focusedLabelColor = Primario,
                    cursorColor = Primario,
                    focusedTextColor = TextoClaro,
                    unfocusedTextColor = TextoClaro
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(icon, contentDescription = if (passwordVisible) "Ocultar" else "Mostrar")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primario,
                    unfocusedBorderColor = TextoClaro.copy(alpha = 0.3f),
                    focusedLabelColor = Primario,
                    cursorColor = Primario,
                    focusedTextColor = TextoClaro,
                    unfocusedTextColor = TextoClaro
                )
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar contraseña") },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(icon, contentDescription = if (confirmPasswordVisible) "Ocultar" else "Mostrar")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primario,
                    unfocusedBorderColor = TextoClaro.copy(alpha = 0.3f),
                    focusedLabelColor = Primario,
                    cursorColor = Primario,
                    focusedTextColor = TextoClaro,
                    unfocusedTextColor = TextoClaro
                )
            )

            Button(
                onClick = {
                    if (password == confirmPassword) {
                        registerViewModel.register(email, password, context) {
                            navController.navigate("login") {
                                popUpTo("register") { inclusive = true }
                            }
                        }
                    } else {
                        registerViewModel.errorMessage.value = "Las contraseñas no coinciden"
                    }
                },
                enabled = !registerViewModel.isLoading.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(containerColor = Primario, contentColor = SobrePrimarioClaro)
            ) {
                Text(if (registerViewModel.isLoading.value) "Registrando..." else "Registrarse")
            }

            registerViewModel.errorMessage.value?.let { errorMsg ->
                Text(errorMsg, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
            }

            Text(registerViewModel.message.value, color = TextoClaro)

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically // Asegura que estén alineados verticalmente en el centro
            ) {
                Text("¿Ya tienes cuenta? ", color = TextoClaro)
                TextButton(onClick = { navController.navigate("login") }) {
                    Text("Inicia sesión", color = Primario)
                }
            }
        }
    }
}
