package com.proyecto.Descuentosya.profile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.proyecto.Descuentosya.ui.theme.*
import com.proyecto.Descuentosya.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    val themeViewModel: ThemeViewModel = viewModel()
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

    val fondo = if (isDarkTheme) FondoOscuro else FondoClaro
    val textoPrincipal = if (isDarkTheme) TextoOscuro else TextoClaro
    val textoSecundario = if (isDarkTheme) TextoOscuroSecundario else TextoClaroSecundario
    val sobrePrimario = if (isDarkTheme) SobrePrimarioOscuro else SobrePrimarioClaro
    val primario = Primario

    val userId = FirebaseAuth.getInstance().currentUser?.uid

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var nacionalidad by remember { mutableStateOf("") }
    var mensajeEstado by remember { mutableStateOf("") }
    var isGuardando by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        userId?.let {
            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(it)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        nombre = doc.getString("nombre") ?: ""
                        apellido = doc.getString("apellido") ?: ""
                        email = doc.getString("email") ?: ""
                        nacionalidad = doc.getString("nacionalidad") ?: ""
                    }
                }
                .addOnFailureListener {
                    mensajeEstado = "Error al cargar datos: ${it.message}"
                }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Editar Perfil", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold), color = textoPrincipal)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = textoPrincipal)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = fondo)
            )
        },
        containerColor = fondo
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .background(fondo)
        ) {
            Text("Información Personal", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textoPrincipal)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {},
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                trailingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = nacionalidad,
                onValueChange = { nacionalidad = it },
                label = { Text("Nacionalidad") },
                leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = {
                    if (userId == null) {
                        mensajeEstado = "Error: Usuario no autenticado"
                        return@Button
                    }
                    if (nombre.isBlank() || apellido.isBlank()) {
                        mensajeEstado = "Por favor, completá tu nombre y apellido"
                        return@Button
                    }

                    isGuardando = true
                    val data = mapOf(
                        "nombre" to nombre.trim(),
                        "apellido" to apellido.trim(),
                        "nacionalidad" to nacionalidad.trim()
                    )
                    FirebaseFirestore.getInstance()
                        .collection("usuarios")
                        .document(userId)
                        .update(data)
                        .addOnSuccessListener {
                            mensajeEstado = "Perfil actualizado correctamente"
                            isGuardando = false
                            navController.popBackStack()
                        }
                        .addOnFailureListener { e ->
                            mensajeEstado = "No se pudo guardar. Intentá de nuevo."
                            isGuardando = false
                            Log.e("Firestore", "Error al actualizar perfil", e)
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primario),
                enabled = !isGuardando
            ) {
                if (isGuardando) {
                    CircularProgressIndicator(color = sobrePrimario, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Guardar Cambios", color = sobrePrimario)
                }
            }

            if (mensajeEstado.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    mensajeEstado,
                    color = if (mensajeEstado.contains("Error", ignoreCase = true)) MaterialTheme.colorScheme.error else primario,
                    fontSize = 14.sp
                )
            }
        }
    }
}
