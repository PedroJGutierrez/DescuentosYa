package com.proyecto.Descuentosya.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import androidx.lifecycle.lifecycleScope
import androidx.activity.ComponentActivity

data class LatLngMarker(val name: String, val latLng: LatLng)

suspend fun buscarRestaurantesCercanos(lat: Double, lng: Double, apiKey: String): List<LatLngMarker> {
    val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
            "?location=$lat,$lng&radius=1500&type=restaurant&keyword=pizza|burger|comida&key=$apiKey"

    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()

    return withContext(Dispatchers.IO) {
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: return@withContext emptyList()

        val markers = mutableListOf<LatLngMarker>()
        val json = JSONObject(body)
        val results = json.getJSONArray("results")

        for (i in 0 until results.length()) {
            val item = results.getJSONObject(i)
            val name = item.getString("name")
            val location = item.getJSONObject("geometry").getJSONObject("location")
            val latitud = location.getDouble("lat")
            val longitud = location.getDouble("lng")
            markers.add(LatLngMarker(name, LatLng(latitud, longitud)))
        }

        markers
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyRestaurantsMapScreen() {
    val context = LocalContext.current
    val mapView = remember { MapView(context).apply { onCreate(Bundle()); onResume() } }
    val locationPermissionGranted = remember { mutableStateOf(false) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        locationPermissionGranted.value = it
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            locationPermissionGranted.value = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Restaurantes Cercanos", color = MaterialTheme.colorScheme.onBackground) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (locationPermissionGranted.value) {
            AndroidView(
                factory = { ctx ->
                    MapsInitializer.initialize(ctx)
                    mapView.getMapAsync { googleMap ->
                        googleMap.uiSettings.isMyLocationButtonEnabled = true
                        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            googleMap.isMyLocationEnabled = true
                        }

                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            location?.let {
                                val userLatLng = LatLng(location.latitude, location.longitude)
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                                googleMap.addMarker(MarkerOptions().position(userLatLng).title("Tu ubicación"))

                                // 🚀 Buscar y agregar marcadores de restaurantes cercanos
                                (ctx as? ComponentActivity)?.lifecycleScope?.launch {
                                    try {
                                        val markers = buscarRestaurantesCercanos(
                                            userLatLng.latitude,
                                            userLatLng.longitude,
                                            "AIzaSyCePVV5wQ5TxhxnThEmJRrGVPXtLtd3zh0" // Reemplazar si cambiás la API KEY
                                        )
                                        markers.forEach {
                                            googleMap.addMarker(MarkerOptions().position(it.latLng).title(it.name))
                                        }
                                    } catch (e: Exception) {
                                        Log.e("MAPA", "Error al obtener restaurantes", e)
                                    }
                                }
                            }
                        }
                    }
                    mapView
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Se requiere permiso de ubicación para mostrar el mapa.", color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}
