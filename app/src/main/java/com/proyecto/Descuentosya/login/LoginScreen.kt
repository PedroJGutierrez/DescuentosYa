package com.proyecto.Descuentosya.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.proyecto.DescuentosYa.R
import com.proyecto.Descuentosya.ui.theme.*
import com.proyecto.Descuentosya.viewmodel.LoginViewModel
import com.airbnb.lottie.compose.*
import com.proyecto.Descuentosya.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val loginViewModel: LoginViewModel = viewModel()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val themeViewModel: ThemeViewModel = viewModel()
    val isDark by themeViewModel.isDarkTheme.collectAsState()

    // Colores dinámicos según el tema
    val fondo = if (isDark) FondoOscuro else FondoClaro
    val textoPrimario = if (isDark) TextoOscuro else TextoClaro
    val sobrePrimario = if (isDark) SobrePrimarioOscuro else SobrePrimarioClaro

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.result
            loginViewModel.handleGoogleSignIn(account, context) {
                navController.navigate("welcome") {
                    popUpTo("welcome") { inclusive = true }
                }
            }
        } catch (e: Exception) {
            loginViewModel.errorMessage.value = "Error al iniciar sesión con Google"
        }
    }

    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso).apply {
            signOut()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iniciar sesión", color = sobrePrimario) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Primario
                )
            )
        },
        containerColor = fondo
    ) { paddingValues ->
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation_welcome))
        val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .height(280.dp)
                    .padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

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
                    unfocusedTextColor = textoPrimario,
                    errorBorderColor = Error,
                    errorLabelColor = Error
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = loginViewModel.passwordError.value,
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = if (passwordVisible) "Ocultar" else "Mostrar")
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
                    unfocusedTextColor = textoPrimario,
                    errorBorderColor = Error,
                    errorLabelColor = Error
                )
            )

            if (loginViewModel.passwordError.value) {
                Text(
                    text = "Contraseña incorrecta",
                    color = Error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Button(
                onClick = {
                    val signInIntent = googleSignInClient.signInIntent
                    launcher.launch(signInIntent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(containerColor = Secundario, contentColor = sobrePrimario)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_gmail),
                    contentDescription = "Logo Google",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Iniciar sesión con Google")
            }

            Button(
                onClick = {
                    loginViewModel.login(email, password, context) {
                        navController.navigate("welcome") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                },
                enabled = !loginViewModel.isLoading.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(containerColor = Primario, contentColor = sobrePrimario)
            ) {
                Text(if (loginViewModel.isLoading.value) "Iniciando sesión..." else "Iniciar sesión")
            }

            loginViewModel.errorMessage.value?.let {
                if (!loginViewModel.passwordError.value) {
                    Text(
                        text = it,
                        color = Error,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                if (loginViewModel.showResendVerification.value) {
                    TextButton(
                        onClick = { loginViewModel.resendVerificationEmail() },
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Text("Reenviar correo de verificación", color = textoPrimario)
                    }
                }
            }

            TextButton(onClick = { navController.navigate("forgot_password") }) {
                Text("¿Olvidaste tu contraseña?", color = textoPrimario)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿No tienes cuenta? ", color = textoPrimario)
                TextButton(onClick = { navController.navigate("register") }) {
                    Text("Regístrate", color = Primario)
                }
            }
        }
    }
}
