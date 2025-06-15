package com.proyecto.Descuentosya.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.proyecto.Descuentosya.components.IconMapper
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController) {
    val context = LocalContext.current
    val db = Firebase.firestore
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var notificacionesActivas by remember { mutableStateOf(true) }

    val categorias = IconMapper.availableIcons
    val estadoCategorias = remember { categorias.associateWith { mutableStateOf(true) } }

    // Cargar preferencias iniciales desde Firestore
    LaunchedEffect(uid) {
        if (uid != null) {
            val doc = db.collection("usuarios").document(uid).get().await()
            notificacionesActivas = (doc.get("notificacionesActivas") as? Boolean) ?: true
            val seleccionadas = (doc.get("notificacionesCategorias") as? List<String>) ?: categorias
            estadoCategorias.forEach { (cat, estado) ->
                estado.value = seleccionadas.contains(cat)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Recibir recordatorios diarios", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Switch(
                checked = notificacionesActivas,
                onCheckedChange = { isChecked ->
                    notificacionesActivas = isChecked
                    val workManager = WorkManager.getInstance(context)

                    if (isChecked) {
                        mostrarNotificacionInstantanea(context, "¡Es un nuevo día, por lo tanto nuevos descuentos!")
                        programarNotificacionesCada12Horas(context)
                        scope.launch { snackbarHostState.showSnackbar("Notificaciones activadas.") }
                    } else {
                        workManager.cancelAllWork()
                        scope.launch { snackbarHostState.showSnackbar("Notificaciones desactivadas.") }
                    }

                    uid?.let {
                        db.collection("usuarios").document(it)
                            .update("notificacionesActivas", isChecked)
                    }
                }
            )

            if (notificacionesActivas) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Indica de qué categoría de descuentos quieres ver notificaciones")
                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        CategoriaToggle(
                            icon = Icons.Default.AllInclusive,
                            label = "Todos",
                            selected = estadoCategorias.values.all { it.value },
                            onToggle = { activarTodo ->
                                estadoCategorias.forEach { (_, estado) -> estado.value = activarTodo }
                                guardarCategorias(uid, estadoCategorias)
                            }
                        )
                    }
                    items(categorias.size) { index ->
                        val categoria = categorias[index]
                        val icono = IconMapper.getIconByName(categoria)
                        val nombre = IconMapper.getCategoryByIconName(categoria)
                        val estado = estadoCategorias[categoria]!!

                        CategoriaToggle(
                            icon = icono,
                            label = nombre,
                            selected = estado.value,
                            onToggle = {
                                estado.value = it
                                guardarCategorias(uid, estadoCategorias)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoriaToggle(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val background = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.2f)
    val iconColor = if (selected) MaterialTheme.colorScheme.primary else Color.Gray

    Column(
        modifier = Modifier
            .width(72.dp)
            .clickable { onToggle(!selected) }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(background, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = iconColor)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = MaterialTheme.typography.labelSmall.fontSize)
    }
}

fun guardarCategorias(
    uid: String?,
    estadoCategorias: Map<String, MutableState<Boolean>>
) {
    uid ?: return
    val db = Firebase.firestore
    val activas = estadoCategorias.filterValues { it.value }.keys.toList()
    db.collection("usuarios").document(uid).update("notificacionesCategorias", activas)
}