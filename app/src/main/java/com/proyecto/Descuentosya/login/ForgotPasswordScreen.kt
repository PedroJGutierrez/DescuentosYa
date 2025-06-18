package com.proyecto.Descuentosya.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.proyecto.Descuentosya.ui.theme.Primario
import com.proyecto.Descuentosya.ui.theme.SobrePrimarioClaro
import com.proyecto.Descuentosya.ui.theme.TextoClaro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recuperar contraseña", color = SobrePrimarioClaro) },
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
                "Ingresa tu correo electrónico",
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
                colors = ButtonDefaults.buttonColors(containerColor = Primario, contentColor = SobrePrimarioClaro)
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
