package com.example.weatherapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.runtime.collectAsState
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
import com.example.weatherapplication.api.responses.WeatherResponse
import com.example.weatherapplication.api.responses.AirQualityResponse
import com.example.weatherapplication.api.responses.WeatherForecastResponse
import com.example.weatherapplication.location.GetUserLocation
import com.example.weatherapplication.nav.BottomNavigationBar
import com.example.weatherapplication.ui.theme.MyAppTheme
import com.example.weatherapplication.ui.theme.ThemeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

sealed class Views(val route: String) {
    data object Home : Views("home_route")
    data object Map : Views("map_route")
    data object Forecast : Views("forecast_route")
    data object Settings : Views("settings_route")
}

class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            MyAppTheme(darkTheme = isDarkTheme) {
                MainApp(themeViewModel, isDarkTheme)
            }
        }
    }
}

@Composable
fun MainApp(themeViewModel: ThemeViewModel, isDarkTheme: Boolean) {
    val context = LocalContext.current

    // State to track latitude, longitude, and whether location is available
    var userLatitude by remember { mutableDoubleStateOf(0.00) }
    var userLongitude by remember { mutableDoubleStateOf(0.00) }
    var locationAvailable by remember { mutableStateOf(false) }

    // State to track loading and weather data
    var isLoading by remember { mutableStateOf(true) }

    // Data
    var weatherData: WeatherResponse? by remember { mutableStateOf(null) }
    var airData: AirQualityResponse? by remember { mutableStateOf(null) }
    var forecastData: WeatherForecastResponse? by remember { mutableStateOf(null) }

    // Function to fetch weather
    fun fetchWeatherData(lat: Double, long: Double) {
        isLoading = true
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val weatherHandler = WeatherHandler()

                // Fetch weather forecast
                weatherHandler.fetchFiveDayForecast(lat, long, object : WeatherHandler.ForecastCallback {
                    override fun onSuccess(forecastResponse: WeatherForecastResponse) {
                        forecastData = forecastResponse

                        Log.d("Debug", "Forecast Data: $forecastData")
                        isLoading = false
                    }

                    override fun onError(errorMessage: String) {
                        Log.e("Forecast", "Error fetching forecast: $errorMessage")
                        isLoading = false
                    }
                })

                // Fetch weather
                weatherHandler.fetchWeather(lat, long, object : WeatherHandler.WeatherCallback {
                    override fun onSuccess(weatherResponse: WeatherResponse) {
                        weatherData = weatherResponse
                        
                        // After weather success, fetch air quality
                        weatherHandler.fetchAirQuality(lat, long, object : WeatherHandler.AirQualityCallback {
                            override fun onSuccess(airQualityResponse: AirQualityResponse) {
                                airData = airQualityResponse

                                Log.d("Debug", "Weather Data: $weatherData, Air Data: $airData, Location: $userLatitude, $userLongitude")
                            }

                            override fun onError(errorMessage: String) {
                                Log.e("AirQuality", "Error fetching air quality: $errorMessage")
                                isLoading = false
                            }
                        })
                    }

                    override fun onError(errorMessage: String) {
                        Log.e("Weather", "Error fetching weather: $errorMessage")
                        isLoading = false
                    }
                })
            } catch (e: Exception) {
                Log.e("Weather", "Error fetching data: ${e.message}")
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
            weatherData != null && airData != null && forecastData != null -> {
                // Parse weatherData, lat, lon to navigation bar so can reuse it on screen components.

                BottomNavigationBar(weatherData!!, airData!!, forecastData!!, userLatitude, userLongitude, themeViewModel, isDarkTheme)
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
            fetchWeatherData(userLatitude, userLongitude)
        }
    }
}