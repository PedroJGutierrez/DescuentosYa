package com.proyecto.Descuentosya.notification


import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.*
import java.util.concurrent.TimeUnit
import java.util.Calendar
import android.app.NotificationManager


fun scheduleNotifications(context: Context) {
    val workManager = WorkManager.getInstance(context)

    // Cancelar cualquier trabajo previo
    workManager.cancelAllWorkByTag("daily_reminders")

    // Notificación de las 08:00
    val morningRequest = buildNotificationRequest(
        context = context,
        hour = 8,
        minute = 0,
        message = "¡Es un nuevo día, por lo tanto nuevos descuentos!",
        tag = "morning_notification"
    )

    // Notificación de las 20:00
    val eveningRequest = buildNotificationRequest(
        context = context,
        hour = 20,
        minute = 0,
        message = "No te olvides de revisar tus descuentos, ¡no lo desaproveches!",
        tag = "evening_notification"
    )

    workManager.enqueueUniqueWork("morning_notification", ExistingWorkPolicy.REPLACE, morningRequest)
    workManager.enqueueUniqueWork("evening_notification", ExistingWorkPolicy.REPLACE, eveningRequest)
}

private fun buildNotificationRequest(
    context: Context,
    hour: Int,
    minute: Int,
    message: String,
    tag: String
): OneTimeWorkRequest {
    val now = Calendar.getInstance()
    val target = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        if (before(now)) {
            add(Calendar.DAY_OF_YEAR, 1)
        }
    }

    val delay = target.timeInMillis - now.timeInMillis

    val inputData = Data.Builder()
        .putString("message", message)
        .build()

    return OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .setInputData(inputData)
        .addTag("daily_reminders")
        .build()
}
fun mostrarNotificacionInstantanea(context: Context, mensaje: String) {
    val notification = NotificationCompat.Builder(context, "default_channel")
        .setContentTitle("Descuentos de hoy")
        .setContentText(mensaje)
        .setSmallIcon(com.proyecto.DescuentosYa.R.drawable.ic_launcher_hdpi)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(999, notification)
}
fun programarNotificacionesCada12Horas(context: Context) {
    val data = Data.Builder()
        .putString("message", "¡Es un nuevo día, por lo tanto nuevos descuentos!")
        .build()

    val request = PeriodicWorkRequestBuilder<NotificationWorker>(12, TimeUnit.HOURS)
        .setInputData(data)
        .addTag("notificaciones_recurrentes")
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "notificaciones_recurrentes",
        ExistingPeriodicWorkPolicy.REPLACE,
        request
    )
}
