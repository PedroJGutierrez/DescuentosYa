package com.proyecto.Descuentosya.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.google.firebase.auth.FirebaseAuth
import com.proyecto.DescuentosYa.R
import com.proyecto.Descuentosya.ui.theme.*
import com.proyecto.Descuentosya.viewmodel.RegisterViewModel
import com.proyecto.Descuentosya.viewmodel.ThemeViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    val registerViewModel: RegisterViewModel = viewModel()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val themeViewModel: ThemeViewModel = viewModel()
    val isDark by themeViewModel.isDarkTheme.collectAsState()

    val fondo = if (isDark) FondoOscuro else FondoClaro
    val textoPrimario = if (isDark) TextoOscuro else TextoClaro
    val sobrePrimario = if (isDark) SobrePrimarioOscuro else SobrePrimarioClaro

    val showSuccessAnimation = remember { mutableStateOf(false) }
    var navigateAfterAnimation by remember { mutableStateOf(false) }

    val user = FirebaseAuth.getInstance().currentUser
    val emailVerified = remember { mutableStateOf(false) }

    // Checkea si el email está verificado y redirige si es así
    LaunchedEffect(user) {
        while (user != null && !user.isEmailVerified && !emailVerified.value) {
            user.reload().addOnSuccessListener {
                if (user.isEmailVerified) {
                    emailVerified.value = true
                }
            }
            delay(4000)
        }
        if (emailVerified.value) {
            FirebaseAuth.getInstance().signOut()
            navController.navigate("login") {
                popUpTo("register") { inclusive = true }
            }
        }
    }

    // Navegación después de animación
    LaunchedEffect(navigateAfterAnimation) {
        if (navigateAfterAnimation) {
            delay(2500)
            navController.navigate("login") {
                popUpTo("register") { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrarse", color = sobrePrimario) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = sobrePrimario)
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Primario)
            )
        },
        containerColor = fondo
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // FORMULARIO
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(if (showSuccessAnimation.value) 16.dp else 0.dp)
                    .background(if (showSuccessAnimation.value) Color.Black.copy(alpha = 0.3f) else Color.Transparent)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Crea tu cuenta", style = MaterialTheme.typography.headlineMedium, color = textoPrimario)

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primario,
                        unfocusedBorderColor = textoPrimario.copy(alpha = 0.3f),
                        focusedLabelColor = Primario,
                        cursorColor = Primario,
                        focusedTextColor = textoPrimario,
                        unfocusedTextColor = textoPrimario
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(icon, contentDescription = null)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primario,
                        unfocusedBorderColor = textoPrimario.copy(alpha = 0.3f),
                        focusedLabelColor = Primario,
                        cursorColor = Primario,
                        focusedTextColor = textoPrimario,
                        unfocusedTextColor = textoPrimario
                    )
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar contraseña") },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(icon, contentDescription = null)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primario,
                        unfocusedBorderColor = textoPrimario.copy(alpha = 0.3f),
                        focusedLabelColor = Primario,
                        cursorColor = Primario,
                        focusedTextColor = textoPrimario,
                        unfocusedTextColor = textoPrimario
                    )
                )

                Button(
                    onClick = {
                        if (password == confirmPassword) {
                            registerViewModel.register(email, password, context) {
                                showSuccessAnimation.value = true
                                navigateAfterAnimation = true
                            }
                        } else {
                            registerViewModel.errorMessage.value = "Las contraseñas no coinciden"
                        }
                    },
                    enabled = !registerViewModel.isLoading.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(containerColor = Primario, contentColor = sobrePrimario)
                ) {
                    Text(if (registerViewModel.isLoading.value) "Registrando..." else "Registrarse")
                }

                registerViewModel.errorMessage.value?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
                }
            }

            if (showSuccessAnimation.value) {
                Dialog(onDismissRequest = { /* No cerrar tocando afuera */ }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .shadow(16.dp, shape = RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SuperficieClara)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation_successful))
                            val progress by animateLottieCompositionAsState(
                                composition,
                                iterations = LottieConstants.IterateForever,
                                speed = 1.0f
                            )

                            LottieAnimation(
                                composition,
                                progress,
                                modifier = Modifier
                                    .size(150.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "¡Registro exitoso!",
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
