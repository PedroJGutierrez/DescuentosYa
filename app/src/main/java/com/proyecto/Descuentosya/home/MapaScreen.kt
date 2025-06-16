package com.proyecto.Descuentosya.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.MapsInitializer
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*

@Composable
fun MapaScreen() {
    val context = LocalContext.current
    var mapView: MapView? = remember { null }

    val locationPermissionGranted = remember { mutableStateOf(false) }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        locationPermissionGranted.value = it
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            locationPermissionGranted.value = true
        }
    }

    if (locationPermissionGranted.value) {
        AndroidView(
            factory = { ctx ->
                mapView = MapView(ctx)
                mapView?.onCreate(null)
                mapView?.onResume()
                MapsInitializer.initialize(ctx)

                mapView?.getMapAsync { googleMap ->
                    googleMap.uiSettings.isMyLocationButtonEnabled = true
                    googleMap.isMyLocationEnabled = true

                    // Centrar en Buenos Aires y buscar restaurantes
                    val latLng = LatLng(-34.6037, -58.3816)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                    if (!Places.isInitialized()) {
                        Places.initialize(ctx, "TU_API_KEY")
                    }

                    val placesClient = Places.createClient(ctx)
                    val request = FindCurrentPlaceRequest.builder(
                        listOf(Place.Field.NAME, Place.Field.LAT_LNG)
                    ).build()

                    placesClient.findCurrentPlace(request)
                        .addOnSuccessListener { response ->
                            response.placeLikelihoods.forEach { likelihood ->
                                if (likelihood.place.name?.contains("Restaurante", ignoreCase = true) == true ||
                                    likelihood.place.name?.contains("Burger", ignoreCase = true) == true ||
                                    likelihood.place.name?.contains("Pizza", ignoreCase = true) == true) {
                                    likelihood.place.latLng?.let {
                                        googleMap.addMarker(MarkerOptions().position(it).title(likelihood.place.name))
                                    }
                                }
                            }
                        }
                }

                mapView!!
            },
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Text("Se requiere permiso de ubicación para mostrar el mapa.")
    }
}
