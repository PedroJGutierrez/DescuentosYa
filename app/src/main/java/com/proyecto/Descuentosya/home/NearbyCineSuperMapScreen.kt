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
import androidx.lifecycle.lifecycleScope
import androidx.activity.ComponentActivity
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

// Reutilizable función para buscar lugares con tipo y keyword personalizados
suspend fun buscarLugares(
    lat: Double,
    lng: Double,
    tipo: String,
    keyword: String,
    apiKey: String,
    radius: Int = 1500
): List<LatLngMarker> {
    val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
            "?location=$lat,$lng&radius=$radius&type=$tipo&keyword=$keyword&key=$apiKey"

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
fun NearbyCinemasMapScreen() {
    NearbyGenericMapScreen(
        tipo = "movie_theater",
        keyword = "cine|peliculas|movie",
        titulo = "Cines Cercanos",
        radius = 100000 // 100 km
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbySupermarketsMapScreen() {
    NearbyGenericMapScreen(
        tipo = "supermarket",
        keyword = "supermercado|market|grocery",
        titulo = "Supermercados Cercanos"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyGenericMapScreen(tipo: String, keyword: String, titulo: String, radius: Int = 1500) {
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
                title = { Text(titulo, color = MaterialTheme.colorScheme.onBackground) },
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

                                (ctx as? ComponentActivity)?.lifecycleScope?.launch {
                                    try {
                                        val markers = buscarLugares(
                                            userLatLng.latitude,
                                            userLatLng.longitude,
                                            tipo,
                                            keyword,
                                            "AIzaSyCePVV5wQ5TxhxnThEmJRrGVPXtLtd3zh0",
                                            radius
                                        )
                                        markers.forEach {
                                            googleMap.addMarker(MarkerOptions().position(it.latLng).title(it.name))
                                        }
                                    } catch (e: Exception) {
                                        Log.e("MAPA", "Error al obtener lugares", e)
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