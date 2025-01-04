package com.example.weatherapplication.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weatherapplication.api.responses.AirQualityResponse
import com.example.weatherapplication.api.responses.WeatherResponse

@Composable
fun HomeScreen(
    weatherData: WeatherResponse,
    airQualityData: AirQualityResponse
) {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
        ) {
            // TODO: ?
        }
        Column(
            modifier = Modifier.fillMaxSize().padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                weatherData.name,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(vertical = 10.dp)
            )
            Text(
                "Data: " + weatherData.weather[0].description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp)
            )

            // Display Air Quality Data
            airQualityData.let { aqData ->
                if (aqData.list.isNotEmpty()) {
                    val currentAQ = aqData.list[0]
                    Text(
                        "Air Quality Index: ${currentAQ.main.aqi}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    Text(
                        "Pollutants:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 10.dp)
                    )

                    with(currentAQ.components) {
                        Text("CO: $co μg/m³")
                        Text("NO2: $no2 μg/m³")
                        Text("O3: $o3 μg/m³")
                        Text("PM2.5: $pm2_5 μg/m³")
                        Text("PM10: $pm10 μg/m³")
                    }
                }
            }
        }
    }
}