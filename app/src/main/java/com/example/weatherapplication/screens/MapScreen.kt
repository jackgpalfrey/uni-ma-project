package com.example.weatherapplication.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.weatherapplication.maps.MapWeatherScreen

@Composable
fun MapScreen(userLatitude: Double, userLongitude: Double) {
    val context = LocalContext.current

    val lat: Double = userLatitude
    val lon: Double = userLongitude

    MapWeatherScreen(lat, lon)
}