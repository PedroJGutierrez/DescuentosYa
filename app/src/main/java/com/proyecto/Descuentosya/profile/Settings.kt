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
import androidx.compose.ui.draw.shadow
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
import com.proyecto.Descuentosya.ui.theme.*
import com.proyecto.Descuentosya.viewmodel.ThemeViewModel
import com.proyecto.Descuentosya.viewmodel.WelcomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val welcomeViewModel: WelcomeViewModel = viewModel()
    val themeViewModel: ThemeViewModel = viewModel()
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

    // Usamos tus colores según tema
    val fondo = if (isDarkTheme) FondoOscuro else FondoClaro
    val textoPrincipal = if (isDarkTheme) TextoOscuro else TextoClaro
    val textoSecundario = if (isDarkTheme) TextoOscuroSecundario else TextoClaroSecundario
    val cardColor = if (isDarkTheme) SuperficieOscura else SuperficieClara

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
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Mi Perfil",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = textoPrincipal
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = textoPrincipal)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { themeViewModel.setDarkTheme(!isDarkTheme) },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.15f), shape = CircleShape)
                            .padding(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (isDarkTheme) R.drawable.sol else R.drawable.luna
                            ),
                            contentDescription = "Cambiar tema",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = { navController.navigate("notifications") }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notificaciones",
                            tint = textoPrincipal
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = fondo,
                    scrolledContainerColor = fondo
                ),
                modifier = Modifier.shadow(elevation = 2.dp)
            )
        },
        containerColor = fondo
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(fondo)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Primario),
                contentAlignment = Alignment.Center
            ) {
                if (nombre.isNotBlank() && apellido.isNotBlank()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$nombre $apellido",
                            color = SobrePrimarioClaro,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = firestoreEmail,
                            color = SobrePrimarioClaro.copy(alpha = 0.8f),
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
                            color = SobrePrimarioClaro.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(SobrePrimarioClaro.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Agregar datos",
                                tint = SobrePrimarioClaro
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Agrega tus datos",
                            color = SobrePrimarioClaro,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingsItem(icon = Icons.Default.Person, title = "Editar perfil", textoPrincipal) {
                        navController.navigate("edit_profile")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = textoSecundario.copy(alpha = 0.15f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))
                    SettingsItem(icon = Icons.Default.PrivacyTip, title = "Privacidad", textoPrincipal) {
                        navController.navigate("privacy")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = textoSecundario.copy(alpha = 0.15f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))
                    SettingsItem(icon = Icons.Default.Security, title = "Seguridad", textoPrincipal) {
                        navController.navigate("security")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = textoSecundario.copy(alpha = 0.15f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))
                    SettingsItem(icon = Icons.Default.AccountCircle, title = "Cuentas", textoPrincipal) {
                        navController.navigate("accounts")
                    }
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
                    Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión", tint = Error)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar sesión", color = Error)
                }
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    textoColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Primario,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = textoColor,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Ir a $title",
            tint = textoColor.copy(alpha = 0.3f),
            modifier = Modifier.size(20.dp)
        )
    }
}