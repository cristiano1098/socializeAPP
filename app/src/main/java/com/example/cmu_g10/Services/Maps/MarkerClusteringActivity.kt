package com.example.cmu_g10.Services.Maps

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import android.location.Geocoder
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.navigation.NavController
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.io.IOException

/**
 * Returns the latitude and longitude of an address
 *
 * @param context
 * @param address
 * @return LatLng
 */
fun geocodeAddress(context: Context, address: String): LatLng? {
    val geocoder = Geocoder(context)
    try {
        val addresses = geocoder.getFromLocationName(address, 1)
        if (addresses != null) {
            if (addresses.isNotEmpty()) {
                val latitude = addresses[0]?.latitude
                val longitude = addresses[0]?.longitude
                return latitude?.let { longitude?.let { it1 -> LatLng(it, it1) } }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

//GoogleMapClustering
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleMapClustering(latitude: Double, longitude: Double, navController: NavController) {
    val cameraPosition = CameraPosition.fromLatLngZoom(LatLng(latitude, longitude), 15f)
    val cameraPositionState = rememberCameraPositionState { position = cameraPosition }

    Box(
        modifier = Modifier.fillMaxSize()


    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {

                // Top app bar with a title and back navigation icon
                TopAppBar(
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                    ),
                    title = {
                        // Title text for the screen
                        Text(
                            "Localização",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    navigationIcon = {

                        // Back navigation icon button
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Filled.KeyboardArrowLeft,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                )
            },
        ) { innerPadding ->
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
            ) {
                Marker(
                    state = MarkerState(position = LatLng(latitude, longitude))
                )
            }
        }
    }
}
