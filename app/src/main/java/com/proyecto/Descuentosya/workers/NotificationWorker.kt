package com.proyecto.descuentosya.workers

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.proyecto.DescuentosYa.R

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val message = inputData.getString("message") ?: return Result.failure()

        // Crear la notificación
        val notification = NotificationCompat.Builder(applicationContext, "default_channel")
            .setContentTitle("Descuentos de hoy")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_hdpi) // Aquí se usa mipmap en lugar de drawable
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Mostrar la notificación
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)

        return Result.success()
    }
}
