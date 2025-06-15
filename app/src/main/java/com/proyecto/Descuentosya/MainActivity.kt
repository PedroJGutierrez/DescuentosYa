package com.proyecto.DescuentosYa

import android.os.Bundle
import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.proyecto.Descuentosya.notification.NotificationWorker
import com.proyecto.Descuentosya.ui.navigation.NavGraph
import com.proyecto.Descuentosya.ui.theme.DescuentosYaTheme
import com.proyecto.Descuentosya.viewmodel.ThemeViewModel
import androidx.work.*
import com.proyecto.Descuentosya.data.FirestoreUploader
import com.proyecto.Descuentosya.data.FavoritosManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirestoreUploader.guardarBeneficiosSiEsNuevoDia(this)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val desdeNotificacion = intent?.getBooleanExtra("desde_notificacion", false) ?: false

        val startDestination = when {
            desdeNotificacion -> "welcome"
            currentUser != null && currentUser.isEmailVerified -> {
                FavoritosManager.cargarFavoritosDesdeFirestore()
                "welcome"
            }
            else -> {
                FavoritosManager.limpiarFavoritos()
                "login"
            }
        }

        setContent {
            DescuentosYaTheme {
                MainApp(startDestination = startDestination)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
        }

        // ✅ Solo programar notificaciones si el usuario las tiene activadas
        currentUser?.uid?.let { uid ->
            FirebaseFirestore.getInstance().collection("usuarios").document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    val notificacionesActivas = doc.getBoolean("notificacionesActivas") ?: true
                    if (notificacionesActivas) {
                        scheduleNotifications()
                        sendImmediateNotification()
                    }
                }
        }
    }

    private fun scheduleNotifications() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        // Cancelar notificaciones anteriores para evitar duplicados
        WorkManager.getInstance(this).cancelAllWorkByTag("daily_notifications")

        scheduleNotificationAtHour(8, "Es un nuevo día, por lo tanto nuevos descuentos!", constraints, "morning_notification")
        scheduleNotificationAtHour(20, "No te olvides de revisar tus descuentos, ¡no lo desaproveches!", constraints, "evening_notification")
    }

    private fun scheduleNotificationAtHour(hour: Int, message: String, constraints: Constraints, tag: String) {
        val now = Calendar.getInstance()
        val target = now.clone() as Calendar
        target.set(Calendar.HOUR_OF_DAY, hour)
        target.set(Calendar.MINUTE, 0)
        target.set(Calendar.SECOND, 0)

        if (target.before(now)) {
            target.add(Calendar.DAY_OF_MONTH, 1)
        }

        val delay = target.timeInMillis - now.timeInMillis

        val data = Data.Builder()
            .putString("message", message)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .setInputData(data)
            .addTag("daily_notifications")
            .addTag(tag)
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)
    }

    private fun sendImmediateNotification() {
        val data = Data.Builder()
            .putString("message", "¡Bienvenido a DescuentosYa! No olvides revisar tus ofertas.")
            .build()

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(0, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("welcome_notification")
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)
    }

    override fun onDestroy() {
        super.onDestroy()
        // FavoritosManager.limpiarFavoritos() // ← Si se desea limpiar al salir completamente
    }
}

@Composable
fun MainApp(startDestination: String) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val themeViewModel: ThemeViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(context.applicationContext as Application)
    )
    val isDarkTheme = themeViewModel.isDarkTheme.collectAsState().value

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                if (!isGranted) {
                    Toast.makeText(
                        context,
                        "No se podrán mostrar notificaciones sin este permiso.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )

        LaunchedEffect(Unit) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    DescuentosYaTheme(darkTheme = isDarkTheme) {
        Surface(modifier = Modifier) {
            NavGraph(
                navController = navController,
                themeViewModel = themeViewModel,
                startDestination = startDestination
            )
        }
    }
}
