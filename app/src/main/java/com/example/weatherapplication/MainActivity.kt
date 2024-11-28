package com.example.weatherapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.weatherapplication.api.WeatherHandler
import com.example.weatherapplication.api.WeatherResponse
import com.example.weatherapplication.location.GetUserLocation
import com.example.weatherapplication.nav.BottomNavigationBar
import com.example.weatherapplication.ui.theme.MyCustomTheme

sealed class Views(val route: String) {
    data object Home : Views("home_route")
    data object Map : Views("map_route")
    data object Forecast : Views("forecast_route")
}

class MainActivity : ComponentActivity() {
    private val apiKey = "f129e0a6aafbd7ec0c71ed808a7b7b53"
    private lateinit var weatherHandler: WeatherHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        weatherHandler = WeatherHandler(apiKey)

        setContent {
            MyCustomTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current

    // State to track latitude, longitude, and whether location is available
    var userLatitude by remember { mutableDoubleStateOf(0.0) }
    var userLongitude by remember { mutableDoubleStateOf(0.0) }

    // Trigger the location fetching
    GetUserLocation(context) { latitude, longitude ->
        userLatitude = latitude as Double
        userLongitude = longitude as Double
    }

    // Trigger weather API call when location is available
    LaunchedEffect(userLatitude, userLongitude) {
        val lat = userLatitude
        val lon = userLongitude

        val weatherHandler = WeatherHandler("f129e0a6aafbd7ec0c71ed808a7b7b53")
        weatherHandler.fetchWeather(lat, lon, object : WeatherHandler.WeatherCallback {
            override fun onSuccess(weatherResponse: WeatherResponse) {
                Log.d("Weather", "Weather: ${weatherResponse.weather}")
            }

            override fun onError(errorMessage: String) {
                Log.e("Weather", "Error fetching weather: $errorMessage")
            }
        })
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        BottomNavigationBar(userLatitude, userLongitude)
    }
}
