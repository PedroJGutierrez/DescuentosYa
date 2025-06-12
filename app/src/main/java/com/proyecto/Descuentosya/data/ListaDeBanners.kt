package com.proyecto.Descuentosya.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.proyecto.Descuentosya.ui.theme.BannerCard
import com.proyecto.Descuentosya.viewmodel.BilleterasViewModel

@Composable
fun ListaBanners(navController: NavController, viewModel: BilleterasViewModel = viewModel()) {
    val billeteras by viewModel.billeteras.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isLoading) {
            item { CircularProgressIndicator() }
        } else {
            items(billeteras) { billetera ->
                BannerCard(
                    billetera = billetera,
                    navController = navController
                )
            }
        }
    }
}
