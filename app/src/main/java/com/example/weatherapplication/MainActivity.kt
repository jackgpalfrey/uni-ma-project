package com.example.weatherapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.weatherapplication.api.WeatherHandler
import com.example.weatherapplication.api.WeatherResponse
import com.example.weatherapplication.location.GetUserLocation
import com.example.weatherapplication.nav.BottomNavigationBar
import com.example.weatherapplication.ui.theme.MyCustomTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

sealed class Views(val route: String) {
    data object Home : Views("home_route")
    data object Map : Views("map_route")
    data object Forecast : Views("forecast_route")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
    var userLatitude by remember { mutableDoubleStateOf(0.00) }
    var userLongitude by remember { mutableDoubleStateOf(0.00) }
    var locationAvailable by remember { mutableStateOf(false) }

    // State to track loading and weather data
    var isLoading by remember { mutableStateOf(true) }
    var weatherData: WeatherResponse? by remember { mutableStateOf(null) }

    // Function to fetch weather
    fun fetchWeather(lat: Double, long: Double) {
        isLoading = true
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val weatherHandler = WeatherHandler()

                weatherHandler.fetchWeather(lat, long, object : WeatherHandler.WeatherCallback {
                    override fun onSuccess(weatherResponse: WeatherResponse) {
                        weatherData = weatherResponse
                        Log.d("Weather","Data: $weatherData")
                        isLoading = false
                    }

                    override fun onError(errorMessage: String) {
                        Log.e("Weather", "Error fetching weather: $errorMessage")
                        isLoading = false
                    }
                })
            } catch (e: Exception) {
                Log.e("Weather", "Error fetching weather: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    // Call GetUserLocation to fetch the user's location
    GetUserLocation(context) { latitude, longitude ->
        userLatitude = latitude
        userLongitude = longitude
        locationAvailable = true
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when {
            weatherData != null -> {
                // Parse weatherData, lat, lon to navigation bar so can reuse it on screen components.

                BottomNavigationBar(weatherData!!, userLatitude, userLongitude)
            }
            else -> {
                // Show an error message if weather data is not available
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center // Center all items vertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(64.dp)
                            .height(64.dp), // Optional: Ensure it's square
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(16.dp)) // Add space between the components
                    Text(
                        text = "Weather data not available",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }

    // Fetch weather data when location is available
    LaunchedEffect(locationAvailable) {
        if (locationAvailable) {
            isLoading = true
            fetchWeather(userLatitude, userLongitude)
        }
    }
}

/*
@SuppressLint("UnrememberedMutableState")
@Composable
fun MainScreen() {
    val context = LocalContext.current

    // State to track latitude, longitude, and whether location is available
    var userLatitude by remember { mutableDoubleStateOf(0.00) }
    var userLongitude by remember { mutableDoubleStateOf(0.00) }

    // Get user location
    GetUserLocation(context) { latitude, longitude ->
        userLatitude = latitude
        userLongitude = longitude
    }

    // Track states
    var isLoading by mutableStateOf(true)
    var weatherData: WeatherResponse? by mutableStateOf(null)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Parse some important information
        BottomNavigationBar(isLoading, weatherData, userLongitude, userLatitude)

        weatherData?.let {
            BottomNavigationBar(
                isLoading,
                it,
                userLatitude,
                userLongitude)
        }
    }

    // Trigger weather API call when location is available
    LaunchedEffect(userLatitude, userLongitude) {
        if (userLatitude == 0.00 && userLongitude == 0.00) {
            // Debugging message
            Log.e("Weather", "Error, longitude and latitude are 0.00")

            return@LaunchedEffect
        }

        val weatherHandler = WeatherHandler()
        weatherHandler.fetchWeather(userLatitude, userLongitude, object : WeatherHandler.WeatherCallback {
            override fun onSuccess(weatherResponse: WeatherResponse) {
                weatherData = weatherResponse
                Log.d("Weather","Data: $weatherData")
                isLoading = false
            }

            override fun onError(errorMessage: String) {
                Log.e("Weather", "Error fetching weather: $errorMessage")
                isLoading = false
            }
        })
    }
}*/
