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
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth()
            )

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

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth()
            )

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

                            userRef.set(oldData + newData)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Datos guardados", Toast.LENGTH_SHORT).show()
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

            Divider(thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))

            Text("Navegar a otras pantallas:", style = MaterialTheme.typography.titleSmall)

            Button(onClick = { navController.navigate("cart") }, modifier = Modifier.fillMaxWidth()) {
                Text("Ir a Carrito")
            }

            Button(onClick = { navController.navigate("search") }, modifier = Modifier.fillMaxWidth()) {
                Text("Ir a Búsqueda")
            }

            Button(onClick = { navController.navigate("notifications") }, modifier = Modifier.fillMaxWidth()) {
                Text("Ir a Notificaciones")
            }

            Button(onClick = { navController.navigate("product_detail") }, modifier = Modifier.fillMaxWidth()) {
                Text("Ir a Detalle de Producto")
            }

            Button(onClick = { navController.navigate("edit_profile") }, modifier = Modifier.fillMaxWidth()) {
                Text("Ir a Editar Perfil")
            }
        }
    }
}
