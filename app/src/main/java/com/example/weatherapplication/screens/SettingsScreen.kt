package com.example.weatherapplication.screens

import android.location.Geocoder
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weatherapplication.api.responses.AirQualityResponse
import com.example.weatherapplication.api.responses.WeatherResponse
import com.example.weatherapplication.ui.theme.ThemeViewModel
import java.util.*

@Composable
fun SettingsScreen(
    weatherData: WeatherResponse,
    airQualityData: AirQualityResponse,
    userLatitude: Double,
    userLongitude: Double,
    themeViewModel: ThemeViewModel
) {
    var locationName by rememberSaveable { mutableStateOf("") }

    locationName = weatherData.name

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Settings Screen")
            Spacer(modifier = Modifier.height(16.dp))

            Text("Adjust Theme")
            Slider(
                value = if (isDarkTheme) 1f else 0f,
                onValueChange = { themeViewModel.setDarkTheme(it > 0.5f) },
                valueRange = 0f..1f,
                steps = 1,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(if (isDarkTheme) "Dark Theme" else "Light Theme")
        }

        // Display user location
        Text("Location: $locationName ($userLatitude, $userLongitude)")
        Spacer(modifier = Modifier.height(16.dp))
    }
}