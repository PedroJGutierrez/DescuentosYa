package com.proyecto.Descuentosya.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.proyecto.DescuentosYa.R
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        crearCanalDeNotificacion(applicationContext)

        runBlocking {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@runBlocking

            val db = FirebaseFirestore.getInstance()
            val userSnapshot = db.collection("usuarios").document(userId).get().await()
            val favoritos = userSnapshot.get("favoritos") as? List<String> ?: return@runBlocking
            val categoriasActivas = userSnapshot.get("notificacionesCategorias") as? List<String> ?: emptyList()

            for ((index, billeteraId) in favoritos.withIndex()) {
                val billeteraSnapshot = db.collection("billeteras").document(billeteraId).get().await()
                val beneficiosRaw = billeteraSnapshot.get("beneficios") as? List<Map<String, Any>> ?: continue

                val beneficiosFiltrados = beneficiosRaw.filter {
                    val disponible = when (val raw = it["disponible"]) {
                        is Boolean -> raw
                        is String -> raw.equals("true", ignoreCase = true)
                        else -> false
                    }
                    val icon = it["icon"]?.toString() ?: ""
                    disponible && categoriasActivas.contains(icon)
                }

                if (beneficiosFiltrados.isEmpty()) continue

                val isBancoCiudad = billeteraId.equals("banco ciudad", ignoreCase = true)
                val titulo = if (isBancoCiudad) "Banco Ciudad" else "BILLETERA $billeteraId"
                val mensaje = if (isBancoCiudad) {
                    "Mirá los beneficios que el Banco Ciudad tiene para vos!"
                } else {
                    "Descuentos disponibles: ${beneficiosFiltrados.size}"
                }

                val resumen = beneficiosFiltrados.joinToString("\n") { beneficio ->
                    val iconName = beneficio["icon"]?.toString() ?: ""
                    val descripcion = beneficio["descripcion"]?.toString() ?: "Descuento disponible"
                    val categoriaLegible = com.proyecto.Descuentosya.components.IconMapper.getCategoryByIconName(iconName)
                    "- $descripcion ($categoriaLegible)"
                }

                val intent = Intent(applicationContext, Class.forName("com.proyecto.DescuentosYa.MainActivity")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("desde_notificacion", true)
                    putExtra("billetera_nombre", billeteraId)
                }

                val pendingIntent = PendingIntent.getActivity(
                    applicationContext,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

                val notification = NotificationCompat.Builder(applicationContext, "default_channel")
                    .setContentTitle(titulo)
                    .setContentText(mensaje)
                    .setStyle(NotificationCompat.BigTextStyle().bigText("Descuentos en:\n$resumen"))
                    .setSmallIcon(R.drawable.ic_launcher_hdpi)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()

                val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(index + 100, notification)
            }
        }

        return Result.success()
    }

    private fun crearCanalDeNotificacion(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "default_channel",
                "Canal de Notificaciones",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Canal para notificaciones diarias de descuentos"
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}