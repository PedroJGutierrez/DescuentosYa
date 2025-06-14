package com.proyecto.Descuentosya.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.proyecto.DescuentosYa.R

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val message = inputData.getString("message") ?: return Result.failure()

        // ✅ Crear canal si no existe
        crearCanalDeNotificacion(applicationContext)

        // Crear la notificación
        val notification = NotificationCompat.Builder(applicationContext, "default_channel")
            .setContentTitle("Descuentos de hoy")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_hdpi)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Mostrar la notificación
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)

        return Result.success()
    }

    private fun crearCanalDeNotificacion(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "default_channel"
            val channelName = "Canal de Notificaciones"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Canal para notificaciones diarias de descuentos"
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}