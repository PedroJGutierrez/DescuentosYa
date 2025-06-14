package com.proyecto.Descuentosya.data

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import com.proyecto.Descuentosya.components.DataManager
import java.time.LocalDate

object FirestoreUploader {

    @RequiresApi(Build.VERSION_CODES.O)
    fun guardarBeneficiosSiEsNuevoDia(context: Context): Boolean {
        val prefs = context.getSharedPreferences("beneficios_prefs", Context.MODE_PRIVATE)
        val ultimoDiaGuardado = prefs.getString("ultimo_dia", null)
        val diaActual = LocalDate.now().toString()

        if (ultimoDiaGuardado == diaActual) return false

        val db = FirebaseFirestore.getInstance()

        DataManager.billeteras.forEach { billetera ->
            val beneficiosMapeados = billetera.beneficios.map { beneficio ->
                val iconName = beneficio.iconName.replace("Filled.", "")
                mapOf(
                    "descripcion" to beneficio.descripcion,
                    "disponible" to beneficio.disponible, // CORREGIDO: Usar directamente el campo disponible
                    "icon" to iconName
                )
            }

            db.collection("billeteras")
                .document(billetera.nombre)
                .set(mapOf("beneficios" to beneficiosMapeados))
        }

        prefs.edit().putString("ultimo_dia", diaActual).apply()
        return true
    }
}