package com.proyecto.Descuentosya.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.proyecto.DescuentosYa.R
import com.proyecto.Descuentosya.ui.theme.PurplePrimary
import com.proyecto.Descuentosya.viewmodel.WelcomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val welcomeViewModel: WelcomeViewModel = viewModel()
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    var firestoreEmail by remember { mutableStateOf("") }
    var paisSeleccionado by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val paises = listOf(
        "Argentina", "Uruguay", "Chile", "Paraguay", "Perú",
        "Bolivia", "Brasil", "México", "Colombia", "España"
    )

    LaunchedEffect(Unit) {
        welcomeViewModel.checkAuthToken(context)
        userId?.let {
            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(it)
                .get()
                .addOnSuccessListener { document ->
                    firestoreEmail = document.getString("email") ?: ""
                    paisSeleccionado = document.getString("pais") ?: ""
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("notifications") // Asegurate que el NavGraph tenga esta ruta
                    }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notificaciones"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // Encabezado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(PurplePrimary),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.icono),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = firestoreEmail,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Selector de país
            Column(modifier = Modifier.padding(horizontal = 32.dp)) {
                Text("País", fontWeight = FontWeight.SemiBold)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { expanded = true }
                        .background(Color.Transparent.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(paisSeleccionado.ifEmpty { "Seleccionar país" })
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    paises.forEach { pais ->
                        DropdownMenuItem(
                            text = { Text(pais) },
                            onClick = {
                                paisSeleccionado = pais
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de actualización
            Button(
                onClick = {
                    userId?.let {
                        FirebaseFirestore.getInstance()
                            .collection("usuarios")
                            .document(it)
                            .update("pais", paisSeleccionado)
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Actualizar")
            }

            // Cerrar sesión
            val isLoggedIn by welcomeViewModel.isLoggedIn.collectAsState()
            if (isLoggedIn) {
                TextButton(
                    onClick = {
                        welcomeViewModel.logout(context)
                        navController.navigate("welcome") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp)
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión", tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar sesión", color = Color.Red)
                }
            }
        }
    }
}
