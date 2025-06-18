package com.proyecto.Descuentosya.home

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.proyecto.Descuentosya.components.Billetera
import com.proyecto.Descuentosya.components.IconMapper
import com.proyecto.Descuentosya.ui.theme.*
import com.proyecto.Descuentosya.viewmodel.ThemeViewModel

@Composable
fun BilleteraDetailScreen(billetera: Billetera, navController: NavController) {
    if (billetera.nombre.equals("Banco Ciudad", ignoreCase = true)) {
        BancoCiudadDetailScreen(navController)
    } else if (billetera.nombre.equals("MODO", ignoreCase = true)) {
        BilleteraModoDetailScreen(navController)
    } else {
        BilleteraDetailDefaultScreen(billetera, navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BilleteraDetailDefaultScreen(
    billetera: Billetera,
    navController: NavController
) {
    val context = LocalContext.current
    val themeViewModel: ThemeViewModel = viewModel()
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

    val fondo = if (isDarkTheme) FondoOscuro else FondoClaro
    val textoPrincipal = if (isDarkTheme) TextoOscuro else TextoClaro
    val textoSecundario = if (isDarkTheme) TextoOscuroSecundario else TextoClaroSecundario
    val cardBackground = if (isDarkTheme) SuperficieOscura else SuperficieClara
    val primario = Primario

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = billetera.nombre,
                        color = textoPrincipal
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = textoPrincipal
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = fondo
                )
            )
        },
        containerColor = fondo
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxSize(),
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Beneficios disponibles:",
                        style = MaterialTheme.typography.titleLarge,
                        color = textoPrincipal,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    billetera.beneficios.forEach { beneficio ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        color = if (beneficio.disponible)
                                            primario
                                        else Color.LightGray,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = IconMapper.getIconByName(beneficio.iconName),
                                    contentDescription = null,
                                    tint = if (beneficio.disponible) Color.White else Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = beneficio.descripcion,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = textoPrincipal
                                )
                                Text(
                                    text = IconMapper.getCategoryByIconName(beneficio.iconName),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textoSecundario
                                )
                            }

                            if (beneficio.disponible) {
                                val categoria = IconMapper.getCategoryByIconName(beneficio.iconName)
                                val rutaMapa = when (categoria) {
                                    "Comida rápida" -> "mapa"
                                    "Cine" -> "mapa_cine"
                                    "Supermercados" -> "mapa_super"
                                    else -> null
                                }
                                rutaMapa?.let {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        IconButton(onClick = {
                                            navController.navigate(it)
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Search,
                                                contentDescription = "Buscar cercanos",
                                                tint = primario
                                            )
                                        }
                                        Text(
                                            text = "Buscar",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = textoSecundario
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    IconButton(onClick = {
                                        val mensaje =
                                            "¡Mirá este descuento de ${beneficio.descripcion} con ${billetera.nombre}! Mira más descuentos así en DescuentosYa!! >> 📲"
                                        val intent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, mensaje)
                                        }
                                        context.startActivity(
                                            Intent.createChooser(intent, "Compartir beneficio con...")
                                        )
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Compartir beneficio",
                                            tint = primario
                                        )
                                    }
                                    Text(
                                        text = "Compartir",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = textoSecundario
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
