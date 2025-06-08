package com.proyecto.DescuentosYa.notifications

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit
import java.util.Calendar
import com.proyecto.descuentosya.workers.NotificationWorker

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
