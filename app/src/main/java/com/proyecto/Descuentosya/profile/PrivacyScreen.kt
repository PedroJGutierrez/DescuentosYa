package com.proyecto.Descuentosya.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.proyecto.Descuentosya.ui.theme.Primario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacidad y Términos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Política de Privacidad",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Primario
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "En DescuentosYa, nos comprometemos a proteger tu información personal. No compartimos ni vendemos tus datos a terceros. Los datos que recopilamos se usan exclusivamente para mejorar tu experiencia en la app, personalizar descuentos y facilitar el acceso a tus billeteras favoritas.",
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Términos de Uso",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Primario
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Al utilizar DescuentosYa, aceptás nuestros términos. Esto incluye respetar los descuentos y promociones ofrecidos, no realizar uso indebido de la app, y mantener actualizada tu cuenta. Nos reservamos el derecho de modificar estos términos con previo aviso.",
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Recolección de Datos",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "La app puede recolectar información básica como nombre, email, billeteras favoritas y ubicación aproximada para mejorar los beneficios mostrados.",
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tus derechos",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Podés solicitar acceso, modificación o eliminación de tus datos en cualquier momento desde la configuración de tu perfil.",
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Contacto",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Si tenés dudas sobre nuestra política de privacidad o los términos de uso, podés contactarnos a soporte@descuentosya.app",
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Última actualización: Junio 2025",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
