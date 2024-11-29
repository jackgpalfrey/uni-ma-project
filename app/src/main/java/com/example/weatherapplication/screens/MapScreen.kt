package com.example.weatherapplication.screens

import androidx.compose.runtime.Composable
import com.example.weatherapplication.maps.SimpleMap

@Composable
fun MapScreen(userLatitude: Double, userLongitude: Double) {
    val lat: Double = userLatitude
    val lon: Double = userLongitude
    SimpleMap(lat, lon)
}