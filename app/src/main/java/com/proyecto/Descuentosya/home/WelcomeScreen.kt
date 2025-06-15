package com.proyecto.Descuentosya.home

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.proyecto.Descuentosya.ui.theme.BannerCard
import com.proyecto.Descuentosya.data.FavoritosManager
import com.proyecto.Descuentosya.components.Billetera

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(navController: NavController) {
    val context = LocalContext.current
    val welcomeViewModel: WelcomeViewModel = viewModel()

    val isLoggedIn by welcomeViewModel.isLoggedIn.collectAsState()
    val userEmail by welcomeViewModel.currentUserEmail.collectAsState()
    val favoritosCargados = FavoritosManager.favoritosCargados.value

    var showWelcomeMessage by remember { mutableStateOf(false) }
    var hasNavigated by remember { mutableStateOf(false) }

    var billeterasFavoritas by remember { mutableStateOf<List<Billetera>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val colorScheme = MaterialTheme.colorScheme

    // Solo dispara la carga inicial
    LaunchedEffect(Unit) {
        FavoritosManager.cargarFavoritosDesdeFirestore()
    }

    // Reacciona a cuando los favoritos ya están cargados
    LaunchedEffect(favoritosCargados) {
        if (favoritosCargados) {
            billeterasFavoritas = FavoritosManager.obtenerBilleterasFavoritas()
            isLoading = false
        }
    }

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
                            color = colorScheme.onBackground
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
                    color = colorScheme.surface.copy(alpha = 0.95f),
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { navController.navigate("billeteras") }) {
                                    Text("Billeteras", color = Color.Black)
                                }
                            }

                            if (showWelcomeMessage) {
                                Text("Bienvenido: $userEmail", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 8.dp))
                                Text("Sesión iniciada correctamente", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 24.dp))
                            }

                            if (isLoading) {
                                CircularProgressIndicator()
                            } else if (billeterasFavoritas.isEmpty()) {
                                Text("Todavía no tienes billeteras favoritas.", style = MaterialTheme.typography.bodyLarge)
                            } else {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(billeterasFavoritas) { billetera ->
                                        BannerCard(
                                            billetera = billetera,
                                            navController = navController,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(150.dp),
                                            onFavoritoCambiado = { nombre, _ ->
                                                billeterasFavoritas = billeterasFavoritas.filterNot { it.nombre == nombre }
                                            }
                                        )
                                    }
                                }
                            }
                        } else {
                            Text("Lo que dicen nuestros usuarios:", style = MaterialTheme.typography.bodyLarge)

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
