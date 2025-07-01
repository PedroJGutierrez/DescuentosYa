package com.proyecto.Descuentosya.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.proyecto.Descuentosya.ui.theme.*
import com.proyecto.Descuentosya.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    val themeViewModel: ThemeViewModel = viewModel()
    val isDark by themeViewModel.isDarkTheme.collectAsState()

    val fondo = if (isDark) FondoOscuro else FondoClaro
    val textoPrimario = if (isDark) TextoOscuro else TextoClaro
    val sobrePrimario = if (isDark) SobrePrimarioOscuro else SobrePrimarioClaro

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recuperar contraseña", color = sobrePrimario) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = sobrePrimario)
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Primario
                )
            )
        },
        containerColor = fondo
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
                "Ingresa tu correo electrónico",
                style = MaterialTheme.typography.headlineMedium,
                color = textoPrimario
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
                    unfocusedBorderColor = textoPrimario.copy(alpha = 0.3f),
                    focusedLabelColor = Primario,
                    cursorColor = Primario,
                    focusedTextColor = textoPrimario,
                    unfocusedTextColor = textoPrimario
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (email.isNotEmpty()) {
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    message = "Correo enviado. Verificá tu bandeja de entrada."
                                    isError = false
                                } else {
                                    message = "Error al enviar correo. Verificá el email ingresado."
                                    isError = true
                                }
                            }
                    } else {
                        message = "El campo no puede estar vacío."
                        isError = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(containerColor = Primario, contentColor = sobrePrimario)
            ) {
                Text("Enviar correo")
            }

            message?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it,
                    color = if (isError) MaterialTheme.colorScheme.error else Primario,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
