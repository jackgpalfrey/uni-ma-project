package com.example.weatherapplication.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.weatherapplication.maps.MapWeatherScreen2
import com.example.weatherapplication.ui.theme.ThemeViewModel

@Composable
fun MapScreen(
    userLatitude: Double,
    userLongitude: Double,
    themeViewModel: ThemeViewModel,
    isDarkMode: Boolean
) {
    val context = LocalContext.current

    val lat: Double = userLatitude
    val lon: Double = userLongitude

    MapWeatherScreen2(lat, lon, themeViewModel, isDarkMode)
}