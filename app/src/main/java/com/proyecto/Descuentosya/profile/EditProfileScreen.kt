package com.proyecto.Descuentosya.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.proyecto.DescuentosYa.R
import com.proyecto.Descuentosya.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val colorScheme = MaterialTheme.colorScheme

    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val focusField = navBackStackEntry?.arguments?.getString("focusField")

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var nacionalidad by remember { mutableStateOf("") }
    var tarjetas by remember { mutableStateOf(listOf<String>()) }
    var nuevaTarjeta by remember { mutableStateOf("") }
    var showTarjetaDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    val tiposTarjetas = listOf("Visa", "Mastercard", "American Express", "Naranja", "Cabal", "Cencosud")

    val focusRequester = remember { FocusRequester() }
    var showFocusMessage by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userId?.let {
            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(it)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        nombre = document.getString("nombre") ?: ""
                        apellido = document.getString("apellido") ?: ""
                        email = document.getString("email") ?: ""
                        nacionalidad = document.getString("nacionalidad") ?: ""
                        tarjetas = document.get("tarjetas") as? List<String> ?: listOf()
                    }
                }
        }
    }

    LaunchedEffect(focusField) {
        if (focusField == "nombre") {
            showFocusMessage = true
            focusRequester.requestFocus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil") },
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
                .background(colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Blue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Mi Información",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            if (showFocusMessage) {
                Text(
                    text = "Agrega un nombre para que se vea en el Perfil",
                    color = Blue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Blue,
                            focusedLabelColor = Blue
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = apellido,
                        onValueChange = { apellido = it },
                        label = { Text("Apellido") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Blue,
                            focusedLabelColor = Blue
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { },
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null)
                        },
                        trailingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "Bloqueado")
                        },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = colorScheme.outline.copy(alpha = 0.5f),
                            disabledLabelColor = colorScheme.onSurface.copy(alpha = 0.6f),
                            disabledTextColor = colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = nacionalidad,
                        onValueChange = { nacionalidad = it },
                        label = { Text("Nacionalidad") },
                        leadingIcon = {
                            Icon(Icons.Default.Place, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Blue,
                            focusedLabelColor = Blue
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    guardarPerfil(userId, nombre, apellido, nacionalidad, tarjetas) { success, msg ->
                        message = msg
                        if (success) {
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blue),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Guardar Cambios", fontSize = 16.sp)
                }
            }

            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    color = if (message.contains("Error")) Color.Red else Blue,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showTarjetaDialog) {
        AlertDialog(
            onDismissRequest = { showTarjetaDialog = false },
            title = { Text("Agregar Tipo de Tarjeta") },
            text = {
                Column {
                    tiposTarjetas.forEach { tipo ->
                        if (tipo !in tarjetas) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        tarjetas = tarjetas + tipo
                                        showTarjetaDialog = false
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CreditCard, contentDescription = null)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(tipo)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTarjetaDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

private fun guardarPerfil(
    userId: String?,
    nombre: String,
    apellido: String,
    nacionalidad: String,
    tarjetas: List<String>,
    onComplete: (Boolean, String) -> Unit
) {
    if (userId == null) {
        onComplete(false, "Error: Usuario no encontrado")
        return
    }

    val data = mapOf(
        "nombre" to nombre,
        "apellido" to apellido,
        "nacionalidad" to nacionalidad,
        "tarjetas" to tarjetas
    )

    FirebaseFirestore.getInstance()
        .collection("usuarios")
        .document(userId)
        .update(data)
        .addOnSuccessListener {
            onComplete(true, "Perfil actualizado correctamente")
        }
        .addOnFailureListener { exception ->
            onComplete(false, "Error al actualizar: ${exception.message}")
        }
}
