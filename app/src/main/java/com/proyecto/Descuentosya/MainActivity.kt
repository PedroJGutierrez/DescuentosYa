package com.proyecto.DescuentosYa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.work.*
import com.proyecto.Descuentosya.MainApp
import com.proyecto.Descuentosya.ui.theme.DescuentosYaTheme
import java.util.Calendar
import java.util.concurrent.TimeUnit
import androidx.work.Data
import com.proyecto.descuentosya.workers.NotificationWorker

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DescuentosYaTheme {
                MainApp()
            }
        }

        // Solicitar permisos en caso de ser Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
        }

        // Programar las notificaciones
        scheduleNotifications()

        // Enviar una notificación inmediata al inicio
        sendImmediateNotification()
    }

    private fun scheduleNotifications() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        // Programar las notificaciones para las 8 AM y 8 PM
        scheduleNotificationAtHour(8, "Es un nuevo día, por lo tanto nuevos descuentos!", constraints)
        scheduleNotificationAtHour(20, "No te olvides de revisar tus descuentos, ¡no lo desaproveches!", constraints)
    }

    private fun scheduleNotificationAtHour(hour: Int, message: String, constraints: Constraints) {
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

        val workRequest = OneTimeWorkRequestBuilder<com.proyecto.descuentosya.workers.NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .setInputData(data)
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)
    }

    // Método para enviar una notificación inmediata al iniciar la app
    private fun sendImmediateNotification() {
        val data = Data.Builder()
            .putString("message", "¡Bienvenido a DescuentosYa! No olvides revisar tus ofertas.")
            .build()

        val workRequest = OneTimeWorkRequestBuilder<com.proyecto.descuentosya.workers.NotificationWorker>()
            .setInitialDelay(0, TimeUnit.MILLISECONDS) // Inmediatamente
            .setInputData(data)
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)
    }
}

