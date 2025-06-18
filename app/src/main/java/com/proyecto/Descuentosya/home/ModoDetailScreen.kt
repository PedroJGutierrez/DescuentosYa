package com.proyecto.Descuentosya.home

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.proyecto.Descuentosya.data.BeneficioScrappeado
import com.proyecto.Descuentosya.ui.theme.*
import com.proyecto.Descuentosya.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BilleteraModoDetailScreen(navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    val beneficios = remember { mutableStateListOf<BeneficioScrappeado>() }
    val context = LocalContext.current
    val themeViewModel: ThemeViewModel = viewModel()
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

    val fondo = if (isDarkTheme) FondoOscuro else FondoClaro
    val textoPrincipal = if (isDarkTheme) TextoOscuro else TextoClaro
    val textoSecundario = if (isDarkTheme) TextoOscuroSecundario else TextoClaroSecundario
    val cardBackground = if (isDarkTheme) SuperficieOscura else SuperficieClara
    val primario = Primario

    LaunchedEffect(Unit) {
        firestore.collection("benefits_modo")
            .get()
            .addOnSuccessListener { result ->
                beneficios.clear()
                beneficios.addAll(result.documents.mapNotNull {
                    it.toObject(BeneficioScrappeado::class.java)
                })
            }
    }

    fun extraerPorcentaje(texto: String): Int {
        val regex = Regex("(\\d{1,2})%")
        val match = regex.find(texto)
        return match?.groupValues?.get(1)?.toIntOrNull() ?: 0
    }

    val beneficiosAgrupados = beneficios
        .groupBy { it.category }
        .mapValues { entry ->
            entry.value.sortedByDescending { beneficio ->
                extraerPorcentaje(beneficio.description)
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MODO", color = textoPrincipal) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = textoPrincipal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = fondo)
            )
        },
        containerColor = fondo
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            beneficiosAgrupados.forEach { (categoria, lista) ->
                item {
                    Text(
                        text = categoria.uppercase(),
                        style = MaterialTheme.typography.titleSmall,
                        color = primario,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(lista) { beneficio ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBackground),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (beneficio.image.isNotEmpty()) {
                                Image(
                                    painter = rememberAsyncImagePainter(beneficio.image),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .padding(end = 8.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = beneficio.title, style = MaterialTheme.typography.bodyMedium, color = textoPrincipal)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = beneficio.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textoSecundario,
                                    maxLines = 3
                                )
                                if (beneficio.conditions.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Condiciones: ${beneficio.conditions}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = textoSecundario
                                    )
                                }
                            }

                            IconButton(onClick = {
                                val mensaje = "¡Mirá este beneficio en MODO! ${beneficio.title}\n${beneficio.description}"
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, mensaje)
                                }
                                context.startActivity(Intent.createChooser(intent, "Compartir beneficio"))
                            }) {
                                Icon(Icons.Default.Share, contentDescription = "Compartir", tint = primario)
                            }
                        }
                    }
                }
            }
        }
    }
}
