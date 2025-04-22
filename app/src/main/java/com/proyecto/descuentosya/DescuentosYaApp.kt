package com.proyecto.descuentosya

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.proyecto.descuentosya.ui.navigation.NavGraph
import com.proyecto.descuentosya.ui.theme.DescuentosYaTheme

class DescuentosYaApp : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DescuentosYaTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    Surface(modifier = Modifier) {
        NavGraph(navController = navController)
    }
}
