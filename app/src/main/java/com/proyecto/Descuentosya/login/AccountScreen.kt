package com.proyecto.Descuentosya.login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(navController: NavController) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var nacionalidad by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    val nacionalidades = listOf(
        "Argentina", "Uruguay", "Chile", "Paraguay", "Perú",
        "Bolivia", "Brasil", "España", "México", "Colombia"
    )
    var nacionalidadExpanded by remember { mutableStateOf(false) }

    // Cargar datos guardados desde Firestore
    LaunchedEffect(userId) {
        if (userId != null) {
            db.collection("usuarios").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        nombre = document.getString("nombre") ?: ""
                        apellido = document.getString("apellido") ?: ""
                        nacionalidad = document.getString("nacionalidad") ?: ""
                        telefono = document.getString("telefono") ?: ""
                    }
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cuenta") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Text("Podés cambiar este dato", style = MaterialTheme.typography.labelSmall)

            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth()
            )
            Text("Podés cambiar este dato", style = MaterialTheme.typography.labelSmall)

            ExposedDropdownMenuBox(
                expanded = nacionalidadExpanded,
                onExpandedChange = { nacionalidadExpanded = !nacionalidadExpanded }
            ) {
                OutlinedTextField(
                    value = nacionalidad,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Nacionalidad") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = nacionalidadExpanded,
                    onDismissRequest = { nacionalidadExpanded = false }
                ) {
                    nacionalidades.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                nacionalidad = opcion
                                nacionalidadExpanded = false
                            }
                        )
                    }
                }
            }
            Text("Seleccioná tu nacionalidad", style = MaterialTheme.typography.labelSmall)

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth()
            )
            Text("Podés cambiar este dato", style = MaterialTheme.typography.labelSmall)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (userId != null) {
                        val userRef = db.collection("usuarios").document(userId)
                        userRef.get().addOnSuccessListener { doc ->
                            val oldData = doc.data ?: emptyMap()
                            val newData = mapOf(
                                "nombre" to nombre,
                                "apellido" to apellido,
                                "nacionalidad" to nacionalidad,
                                "telefono" to telefono
                            )

                            // Merge con los datos anteriores
                            userRef.set(oldData + newData)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Datos guardados", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        Toast.makeText(context, "No hay usuario autenticado", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }
        }
    }
}


