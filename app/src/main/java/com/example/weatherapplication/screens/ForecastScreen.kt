package com.example.weatherapplication.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import com.example.weatherapplication.maps.SimpleMap
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapplication.api.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL


@Composable
fun ForecastScreen(data: WeatherResponse) {
    // State variables for loading, error, and weather data
    var weatherData by remember { mutableStateOf<WeatherResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch weather data once when the screen is composed
    LaunchedEffect(Unit) {
        try {
//            val data = fetchWeatherData("London") // Replace "London" with your desired location
            weatherData = data
        } catch (e: Exception) {
            errorMessage = e.message ?: "An unknown error occurred."
        } finally {
            isLoading = false
        }
    }

    // UI content
    when {
        isLoading -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        }
        errorMessage != null -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text("Error: $errorMessage")
            }
        }
        weatherData != null -> {
            WeatherForecast(weatherData!!)
        }
    }
}

@Composable
fun WeatherForecast(weatherData: WeatherResponse) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("Weather Forecast for ${weatherData.weather[0].main}", style = MaterialTheme.typography.headlineMedium)
        }
        item {
            Text("Temperature: ${weatherData.main.temp}°C")
            Text("Condition: ${weatherData.weather[0].description}")
        }
        item {
            Text("Humidity: ${weatherData.main.humidity}%")
            Text("Wind Speed: ${weatherData.wind.speed} m/s")
        }
    }
}

data class WeatherData(
    val cityName: String,
    val temperature: Double,
    val condition: String,
    val humidity: Int,
    val windSpeed: Double
)

// Function to fetch weather data from the API
suspend fun fetchWeatherData(city: String): WeatherData {
    val apiKey = "f129e0a6aafbd7ec0c71ed808a7b7b53"
    val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$apiKey"

    return withContext(Dispatchers.IO) {
        val response = URL(url).readText()
        val jsonObject = JSONObject(response)

        val cityName = jsonObject.getString("name")
        val main = jsonObject.getJSONObject("main")
        val weather = jsonObject.getJSONArray("weather").getJSONObject(0)
        val wind = jsonObject.getJSONObject("wind")

        WeatherData(
            cityName = cityName,
            temperature = main.getDouble("temp"),
            condition = weather.getString("description"),
            humidity = main.getInt("humidity"),
            windSpeed = wind.getDouble("speed")
        )
    }
}
