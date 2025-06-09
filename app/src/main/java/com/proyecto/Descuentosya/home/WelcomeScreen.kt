package com.proyecto.Descuentosya.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.proyecto.DescuentosYa.R
import com.proyecto.Descuentosya.viewmodel.WelcomeViewModel
import com.proyecto.Descuentosya.ui.theme.FondoCelesteBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(navController: NavController) {
    val context = LocalContext.current
    val welcomeViewModel: WelcomeViewModel = viewModel()

    val isLoggedIn by welcomeViewModel.isLoggedIn.collectAsState()
    val userEmail by welcomeViewModel.currentUserEmail.collectAsState()

    var showWelcomeMessage by remember { mutableStateOf(false) }
    var hasNavigated by remember { mutableStateOf(false) }

    LaunchedEffect(isLoggedIn) {
        welcomeViewModel.checkAuthToken(context)

        if (isLoggedIn && !hasNavigated) {
            hasNavigated = true

            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                Firebase.firestore.collection("usuarios").document(uid)
                    .get()
                    .addOnSuccessListener { doc ->
                        val tipo = doc.getString("tipo") ?: "Usuario"

                        showWelcomeMessage = !welcomeViewModel.hasShownWelcome(context)
                        welcomeViewModel.setWelcomeShown(context)


                    }
            }
        }
    }

    FondoCelesteBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Descuentos Ya",
                            style = MaterialTheme.typography.displayLarge.copy(
                                shadow = Shadow(
                                    color = Color.Black.copy(alpha = 0.4f),
                                    offset = Offset(2f, 2f),
                                    blurRadius = 4f
                                )
                            ),
                            color = Color.Black
                        )
                    },
                    actions = {
                        if (isLoggedIn) {
                            IconButton(onClick = { navController.navigate("settings") }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menú")
                            }
                        }
                    }
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.TopCenter
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = Color.White.copy(alpha = 0.9f),
                    tonalElevation = 4.dp,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isLoggedIn) {
                            if (showWelcomeMessage) {
                                Text(
                                    "Bienvenido: $userEmail",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    "Sesión iniciada correctamente",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(bottom = 24.dp)
                                )
                            }

                            Button(
                                onClick = { navController.navigate("billeteras") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = MaterialTheme.shapes.large
                            ) {
                                Text("Billeteras")
                            }

                            Button(
                                onClick = { navController.navigate("billeteras_favoritas") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = MaterialTheme.shapes.large
                            ) {
                                Text("Billeteras Favoritas")
                            }

                            OutlinedButton(
                                onClick = {
                                    welcomeViewModel.logout(context)
                                    navController.navigate("welcome") {
                                        popUpTo("welcome") { inclusive = true }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                shape = MaterialTheme.shapes.large
                            ) {
                                Text("Cerrar Sesión")
                            }
                        } else {
                            Text(
                                "Lo que dicen nuestros usuarios:",
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(
                                    listOf(
                                        R.drawable.persona1,
                                        R.drawable.persona2,
                                        R.drawable.persona3
                                    )
                                ) { image ->
                                    Card(
                                        modifier = Modifier
                                            .width(250.dp)
                                            .height(300.dp),
                                        shape = MaterialTheme.shapes.large
                                    ) {
                                        Image(
                                            painter = painterResource(id = image),
                                            contentDescription = "Usuario feliz",
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { navController.navigate("login") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = MaterialTheme.shapes.large
                            ) {
                                Text("Iniciar Sesión")
                            }

                            OutlinedButton(
                                onClick = { navController.navigate("register") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = MaterialTheme.shapes.large
                            ) {
                                Text("Registrarse")
                            }
                        }
                    }
                }
            }
        }
    }
}
