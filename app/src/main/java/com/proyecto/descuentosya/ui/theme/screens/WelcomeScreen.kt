package com.example.descuentosya.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.proyecto.descuentosya.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(navController: NavController) {
    val context = LocalContext.current
    val welcomeViewModel: WelcomeViewModel = viewModel()
    val isLoggedIn by welcomeViewModel.isLoggedIn.collectAsState(initial = false)

    LaunchedEffect(key1 = context) {
        welcomeViewModel.checkAuthToken(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Descuentos Ya") },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Text(
            "Descuentos Ya",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoggedIn) {
            Text(
                "Sesión iniciada correctamente",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

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
        }}}}



