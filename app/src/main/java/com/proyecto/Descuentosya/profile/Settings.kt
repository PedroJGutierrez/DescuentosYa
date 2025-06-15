package com.proyecto.Descuentosya.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
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
import com.proyecto.Descuentosya.viewmodel.ThemeViewModel
import com.proyecto.Descuentosya.viewmodel.WelcomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val welcomeViewModel: WelcomeViewModel = viewModel()
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val colorScheme = MaterialTheme.colorScheme

    var firestoreEmail by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        welcomeViewModel.checkAuthToken(context)
        userId?.let {
            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(it)
                .get()
                .addOnSuccessListener { document ->
                    firestoreEmail = document.getString("email") ?: ""
                    nombre = document.getString("nombre") ?: ""
                    apellido = document.getString("apellido") ?: ""
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
                    val themeViewModel: ThemeViewModel = viewModel()
                    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

                    IconButton(onClick = { themeViewModel.setDarkTheme(!isDarkTheme) }) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.Gray.copy(alpha = 0.2f), shape = CircleShape)
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (isDarkTheme) R.drawable.luna_mitad else R.drawable.luna_blanca
                                ),
                                contentDescription = "Cambiar tema",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(onClick = {
                        navController.navigate("notifications")
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
                .background(colorScheme.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Blue),
                contentAlignment = Alignment.Center
            ) {
                if (nombre.isNotBlank() && apellido.isNotBlank()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$nombre $apellido",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = firestoreEmail,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            navController.navigate("edit_profile?focusField=nombre")
                        }
                    ) {
                        Text(
                            text = "Agrega un nombre para que se vea en el Perfil",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Agregar datos",
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Agrega tus datos",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
            ) {
                Column {
                    SettingsItem(
                        icon = Icons.Default.Person,
                        title = "Editar perfil",
                        onClick = { navController.navigate("edit_profile") }
                    )

                    Divider(color = colorScheme.outline.copy(alpha = 0.2f))
                    SettingsItem(
                        icon = Icons.Default.PrivacyTip,
                        title = "Privacidad",
                        onClick = { navController.navigate("privacy") }
                    )
                    Divider(color = colorScheme.outline.copy(alpha = 0.2f))
                    SettingsItem(
                        icon = Icons.Default.Security,
                        title = "Seguridad",
                        onClick = { navController.navigate("security") }
                    )
                    Divider(color = colorScheme.outline.copy(alpha = 0.2f))

                    SettingsItem(
                        icon = Icons.Default.AccountCircle,
                        title = "Cuentas",
                        onClick = { navController.navigate("accounts") }
                    )
                    Divider(color = colorScheme.outline.copy(alpha = 0.2f))

                }
            }

            Spacer(modifier = Modifier.weight(1f))

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
                        .padding(bottom = 32.dp)
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión", tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar sesión", color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Ir a $title",
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.size(20.dp)
        )
    }
}