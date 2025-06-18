package com.proyecto.Descuentosya.profile

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.proyecto.Descuentosya.ui.theme.Primario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(navController: NavController) {
    val context = LocalContext.current

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var errorField by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val user = FirebaseAuth.getInstance().currentUser

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacidad") },
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
            verticalArrangement = Arrangement.Top
        ) {
            Text("Cambiar Contraseña", style = MaterialTheme.typography.headlineSmall, color = Primario)
            Spacer(modifier = Modifier.height(24.dp))

            PasswordField(
                label = "Contraseña actual",
                password = currentPassword,
                onPasswordChange = { currentPassword = it },
                visible = currentPasswordVisible,
                onVisibilityChange = { currentPasswordVisible = it },
                isError = errorField == "actual"
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordField(
                label = "Nueva contraseña",
                password = newPassword,
                onPasswordChange = { newPassword = it },
                visible = newPasswordVisible,
                onVisibilityChange = { newPasswordVisible = it },
                isError = errorField == "nueva"
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordField(
                label = "Confirmar nueva contraseña",
                password = confirmPassword,
                onPasswordChange = { confirmPassword = it },
                visible = confirmPasswordVisible,
                onVisibilityChange = { confirmPasswordVisible = it },
                isError = errorField == "confirmar"
            )

            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (newPassword != confirmPassword) {
                        errorField = "confirmar"
                        errorMessage = "Las contraseñas no coinciden."
                        return@Button
                    }

                    if (user != null && user.email != null) {
                        val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
                        user.reauthenticate(credential).addOnCompleteListener { authTask ->
                            if (authTask.isSuccessful) {
                                user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        Toast.makeText(context, "Contraseña cambiada correctamente", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack() // Volver a Settings
                                    } else {
                                        errorField = "nueva"
                                        errorMessage = updateTask.exception?.message ?: "Error al cambiar la contraseña"
                                    }
                                }
                            } else {
                                errorField = "actual"
                                errorMessage = "Contraseña actual incorrecta"
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar cambios")
            }
        }
    }
}

@Composable
fun PasswordField(
    label: String,
    password: String,
    onPasswordChange: (String) -> Unit,
    visible: Boolean,
    onVisibilityChange: (Boolean) -> Unit,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isError) Modifier.border(1.dp, Color.Red, MaterialTheme.shapes.medium) else Modifier),
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = { onVisibilityChange(!visible) }) {
                Icon(
                    imageVector = if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña"
                )
            }
        },
        singleLine = true,
        isError = isError
    )
}
