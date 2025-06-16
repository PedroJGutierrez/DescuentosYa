package com.proyecto.Descuentosya.profile

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.proyecto.DescuentosYa.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

enum class CuentaTipo { WHATSAPP, TELEGRAM }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var tipoCuenta by remember { mutableStateOf<CuentaTipo?>(null) }
    var inputValue by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf("") }

    var whatsapp by remember { mutableStateOf<String?>(null) }
    var telegram by remember { mutableStateOf<String?>(null) }
    var gmail by remember { mutableStateOf<String?>(null) }
    var googlePhoto by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(userId) {
        userId?.let {
            val doc = FirebaseFirestore.getInstance().collection("usuarios").document(it).get().await()
            whatsapp = doc.getString("whatsapp")
            telegram = doc.getString("telegram")
            gmail = doc.getString("gmail")

            doc.getString("foto_google")?.let { url ->
                val loader = ImageLoader(context)
                val request = ImageRequest.Builder(context).data(url).allowHardware(false).build()
                val result = (loader.execute(request) as? SuccessResult)?.drawable
                googlePhoto = (result as? android.graphics.drawable.BitmapDrawable)?.bitmap
            }
        }
    }

    fun actualizarCampo(campo: String, valor: String) {
        guardarCampoFirestore(userId, campo, valor)
        when (campo) {
            "whatsapp" -> whatsapp = valor
            "telegram" -> telegram = valor
            "gmail" -> gmail = valor
        }
    }

    fun eliminarCampo(campo: String) {
        userId?.let {
            val updates = hashMapOf<String, Any?>(campo to null)
            FirebaseFirestore.getInstance().collection("usuarios").document(it).update(updates)
        }
        when (campo) {
            "gmail" -> gmail = null
        }
    }

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (gmail != null) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Cuenta ya asociada")
            }
            return@rememberLauncherForActivityResult
        }

        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            account?.let {
                val fullName = it.displayName ?: ""
                val firstName = fullName.split(" ").firstOrNull() ?: ""
                val lastName = fullName.split(" ").drop(1).joinToString(" ")
                val email = it.email ?: ""
                val photoUrl = it.photoUrl?.toString() ?: ""

                userId?.let { uid ->
                    val updates = mapOf(
                        "nombre" to firstName,
                        "apellido" to lastName,
                        "gmail" to email,
                        "foto_google" to photoUrl
                    )
                    FirebaseFirestore.getInstance().collection("usuarios").document(uid)
                        .update(updates)
                }

                gmail = email

                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Cuenta Google vinculada con éxito!")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cuentas vinculadas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Selecciona una cuenta para vincularla", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(32.dp))

            BotonCuenta("WhatsApp", R.drawable.ic_whatsapp, whatsapp) {
                tipoCuenta = CuentaTipo.WHATSAPP
                inputValue = ""
                showDialog = true
            }

            BotonCuenta("Telegram", R.drawable.ic_telegram, telegram) {
                tipoCuenta = CuentaTipo.TELEGRAM
                inputValue = ""
                showDialog = true
            }

            BotonCuentaGoogle("Google", googlePhoto, gmail) {
                if (gmail != null) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Cuenta ya asociada")
                    }
                } else {
                    val signInClient = GoogleSignIn.getClient(context, gso)
                    signInClient.signOut().addOnCompleteListener {
                        googleLauncher.launch(signInClient.signInIntent)
                    }
                }
            }

            if (gmail != null) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = {
                    eliminarCampo("gmail")
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Cuenta Google desvinculada")
                    }
                }) {
                    Text("Desvincular cuenta")
                }
            }

            if (showDialog && tipoCuenta != null) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = {
                        Text("Vincular ${tipoCuenta!!.name.lowercase().replaceFirstChar { it.uppercase() }}")
                    },
                    text = {
                        Column {
                            if (tipoCuenta == CuentaTipo.WHATSAPP) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "+54 9 ",
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                    OutlinedTextField(
                                        value = inputValue,
                                        onValueChange = {
                                            inputValue = it.filter { c -> c.isDigit() }
                                            inputError = ""
                                        },
                                        label = { Text("Ej: 1123456789") },
                                        isError = inputError.isNotEmpty(),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            } else {
                                OutlinedTextField(
                                    value = inputValue,
                                    onValueChange = {
                                        inputValue = it
                                        inputError = ""
                                    },
                                    label = { Text("Usuario Telegram (@usuario)") },
                                    isError = inputError.isNotEmpty(),
                                    singleLine = true
                                )
                            }
                            if (inputError.isNotEmpty()) {
                                Text(
                                    text = inputError,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val esValido = when (tipoCuenta) {
                                CuentaTipo.WHATSAPP -> inputValue.matches(Regex("^\\d{8,10}$"))
                                CuentaTipo.TELEGRAM -> inputValue.matches(Regex("^@?[a-zA-Z0-9_]{5,}$"))
                                else -> false
                            }

                            if (!esValido) {
                                inputError = "Formato inválido"
                                return@TextButton
                            }

                            val finalValue = when (tipoCuenta) {
                                CuentaTipo.TELEGRAM -> if (inputValue.startsWith("@")) inputValue else "@${inputValue}"
                                CuentaTipo.WHATSAPP -> "+549$inputValue"
                                else -> inputValue
                            }

                            actualizarCampo(tipoCuenta!!.name.lowercase(), finalValue)
                            showDialog = false

                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Datos guardados exitosamente!")
                            }
                        }) {
                            Text("Guardar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancelar")
                        }
                    },
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }
}

@Composable
fun BotonCuenta(nombre: String, iconResId: Int, valorActual: String?, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .clickable { onClick() }
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = nombre,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(nombre, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        valorActual?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = it, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun BotonCuentaGoogle(nombre: String, imagen: Bitmap?, valorActual: String?, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .clickable { onClick() }
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (imagen != null) {
            Image(bitmap = imagen.asImageBitmap(), contentDescription = nombre, modifier = Modifier.size(48.dp))
        } else {
            Image(painter = painterResource(id = R.drawable.ic_gmail), contentDescription = nombre, modifier = Modifier.size(48.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(nombre, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        valorActual?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = it, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

fun guardarCampoFirestore(userId: String?, campo: String, valor: String) {
    userId ?: return
    val db = FirebaseFirestore.getInstance()
    db.collection("usuarios").document(userId).update(campo, valor)
}
