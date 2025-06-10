package com.proyecto.Descuentosya.data

import com.google.firebase.firestore.FirebaseFirestore
import com.proyecto.Descuentosya.components.Billetera
import com.proyecto.Descuentosya.components.Beneficio
import com.proyecto.Descuentosya.components.IconMapper
import kotlinx.coroutines.tasks.await

object BilleterasRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun obtenerBilleteras(): List<Billetera> {
        val snapshot = db.collection("billeteras").get().await()
        return snapshot.documents.mapNotNull { doc ->
            val nombre = doc.id
            val beneficiosData = doc.get("beneficios") as? List<Map<String, Any>> ?: return@mapNotNull null

            val beneficios = beneficiosData.mapNotNull { map ->
                val iconName = map["icon"] as? String ?: return@mapNotNull null
                val disponible = map["disponible"] as? Boolean ?: false

                // Generar descripción basada en el icono y disponibilidad
                val descripcion = map["descripcion"] as? String
                    ?: IconMapper.generateDescriptionForIcon(iconName, disponible)

                val icon = IconMapper.getIconByName(iconName)

                Beneficio(
                    icon = icon,
                    disponible = disponible,
                    descripcion = descripcion,
                    iconName = iconName
                )
            }

            Billetera(nombre = nombre, beneficios = beneficios)
        }
    }
}