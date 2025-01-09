package com.example.weatherapplication.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices

@Composable
fun GetUserLocation(context: Context, onLocationAvailable: (Double, Double) -> Unit) {
    val fusedLocationProviderClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val locationPermissionState = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val userLatitude = location.latitude
                        val userLongitude = location.longitude

                        Log.d("UserLocation", "Lat: $userLatitude, Long: $userLongitude")

                        // Call the callback function with the retrieved latitude and longitude
                        onLocationAvailable(userLatitude, userLongitude)
                    } else {
                        val userLatitude = 30.046080 // UWE Campus Bristol
                        val userLongitude = -33.018660

                        onLocationAvailable(userLatitude, userLongitude)

                        Log.d("UserLocation", "Cannot find UserLocation using Mobile GPS null")
                        Log.d("UserLocation", "Setting Default location to UWE Campus")
                    }
                }
            } else {
                throw SecurityException("Location permission not granted.")
            }
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Launch the permission request when the composable is first recomposed
    LaunchedEffect(Unit) {
        locationPermissionState.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}