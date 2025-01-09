package com.example.weatherapplication.screens

import android.location.Geocoder
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp)
    ) {
        val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "General",
            style = MaterialTheme.typography.titleLarge
        )

        HorizontalDivider(
            modifier = Modifier.padding(0.dp, 12.dp)
        )

        Switch(
            modifier = Modifier.semantics { contentDescription = "Demo" }
                .padding(0.dp, 8.dp),
            checked = isDarkTheme,
            onCheckedChange = { themeViewModel.setDarkTheme(it) }
        )
        Text(
            modifier = Modifier.padding(0.dp),
            text = if (isDarkTheme) "Dark Mode" else "Light Mode",
            style = MaterialTheme.typography.bodySmall
        )

        // Display user location
        Text("Location: $locationName ($userLatitude, $userLongitude)")
        Spacer(modifier = Modifier.height(16.dp))
    }
}