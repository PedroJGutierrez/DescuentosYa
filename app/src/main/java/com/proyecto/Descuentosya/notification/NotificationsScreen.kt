package com.proyecto.Descuentosya.notification

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.work.WorkManager
import com.proyecto.Descuentosya.notification.scheduleNotifications
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController) {
    val context = LocalContext.current
    var notificacionesActivas by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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
                        // 🔔 Notificación inmediata
                        mostrarNotificacionInstantanea(
                            context,
                            "¡Es un nuevo día, por lo tanto nuevos descuentos!"
                        )

                        // ⏰ Programar cada 12 horas desde ahora
                        programarNotificacionesCada12Horas(context)

                        scope.launch {
                            snackbarHostState.showSnackbar("Notificaciones activadas.")
                        }
                    } else {
                        // ❌ Cancelar TODAS las notificaciones de la app
                        workManager.cancelAllWork()

                        scope.launch {
                            snackbarHostState.showSnackbar("Notificaciones desactivadas.")
                        }
                    }
                }
            )
        }
    }
}

